#include "src/adapter/agent_adapter.h"
#include "src/client/client.h"

namespace xlog
{

Client::Client(const ::Ice::StringSeq& defaultAgents,
        const bool is_udp_protocol, const int maxQueueSize) :
         _defaultAgents(defaultAgents), _is_udp_protocol(is_udp_protocol), _maxQueueSize(maxQueueSize)
{
   _agentAdapter = new AgentAdapter;

   bool flag=_agentAdapter->init(_defaultAgents,_is_udp_protocol);
   if (flag)
   { 
      std::cout << "success to init agent adapter!" << std::endl;
      start().detach(); 
   } else
   {
      std::cerr << "failt to init agent adapter!" << std::endl;
   }
}

bool Client::doSend(const slice::LogDataSeq& data)
{
    ::IceUtil::Monitor<IceUtil::Mutex>::Lock lock(_dataMutex);
    if (_data.size() >= _maxQueueSize)
    {
        std::cerr << "Client::append queue is full, maxQueueSize is " << _maxQueueSize << std::endl;
        return false;
    }

    _data.insert(_data.end(), data.begin(), data.end());

    _dataMutex.notify();

    return true;
}

void Client::run()
{
    for (;;)
    {
        slice::LogDataSeq data;
        {
            ::IceUtil::Monitor<IceUtil::Mutex>::Lock lock(_dataMutex);
            if (_data.empty())
            {    
                _dataMutex.wait();
            }
            if(_data.size()>5)
            {
               slice::LogDataSeq::iterator begin_it=_data.begin();
               slice::LogDataSeq::iterator end_it=begin_it+5;
               data.assign(begin_it,end_it);
                _data.erase(begin_it,end_it);
            }else
            {
               data.swap(_data);
            }
            _dataMutex.notify();
        }
        if(!_agentAdapter->send(data)) {
           std::cerr << "Fail to send data to agent,data count:"<< data.size()<< std::endl;
        } 
    }
}

void Client::close()
{
   while(!_data.empty())
   {
      sleep(2);
   }   
}
}
