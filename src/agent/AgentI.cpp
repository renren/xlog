#include <sys/types.h>
#include <sys/stat.h>

#include "src/config/agent_config_manager.h"
#include "src/config/client_config_manager.h"
#include "src/config/dispatcher_config.h"
#include "src/adapter/dispatcher_adapter.h"
#include "src/agent/AgentI.h"

namespace xlog
{

AgentI::AgentI()
{
}

void AgentI::init(const Ice::CommunicatorPtr& ic, const ZKConnectionPtr& conn,const std::string& w_list)
{
    std::cout << "AgentI::init step " << __LINE__ << std::endl;
    _config_dispatcher = DispatcherConfigPtr(new DispatcherConfig(conn));
    std::cout << "AgentI::init step " << __LINE__ << std::endl;
    _config_dispatcher->init();
    std::cout << "AgentI::init step " << __LINE__ << std::endl;
    _adapter_dispatcher = new DispatcherAdapter(ic, _config_dispatcher);
    std::cout << "AgentI::init step " << __LINE__ << std::endl;
    _adapter_dispatcher->init();
    std::cout << "AgentI::init step " << __LINE__ << std::endl;
    _normalSendWorker = new NormalSendWorker(_adapter_dispatcher);
    std::cout << "AgentI::init step " << __LINE__ << std::endl;
    _normalSendWorker->start().detach();

    wlm = new WhiteListManager();
    wlm->initialize(w_list);
    std::cout << "AgentI::init while_list step " << __LINE__ << std::endl;
    wlm->start().detach();
   // _failedSendWorker = new FailedSendWorker;
   // std::cout << "AgentI::init step " << __LINE__ << std::endl;
   // _failedSendWorker->start().detach();
   // std::cout << "AgentI::init step " << __LINE__ << std::endl;
}

void AgentI::add(const slice::LogDataSeq& data, const ::Ice::Current& current)
{
    if(wlm->doValidate(data))
    {
      _normalSendWorker->add(data);
    }
}
/**
bool AgentI::doValidate(const slice::LogDataSeq& data)
{
   slice::LogDataSeq::const_iterator ld_it;
   for(ld_it = data.begin();ld_it != data.end(); ld_it++)
   {
     Ice::StringSeq::const_iterator c_it;
     bool flag=true;
     std::string category;
     int pos=0;
     for(c_it=(*ld_it).categories.begin();c_it!=(*ld_it).categories.end();c_it++)
     {
       if(pos==0)
       {
         pos=1;
       }else
       {
         category.append("/");  
       }
       category.append(*c_it);
     }
     Ice::StringSeq::iterator w_l_it;
     for(w_l_it=white_list.begin();w_l_it!=white_list.end();w_l_it++)
     {
       if(*w_l_it==category)
       {
         flag=false;
         break;
       }
     }
     if(!flag){
       std::cout << "AgentI::add LogData "<<__LINE__ <<".It does not support categories "<< category << std::endl;
       return false;
     }
   }
   return true;
}
**/
void AgentI::addFailedLogData(const slice::LogDataSeq& data, const ::Ice::Current& current)
{
    _failedSendWorker->add(data);
}

::Ice::StringSeq AgentI::subscribeClient(const std::string& prxStr, const ::Ice::Current& current)
{
//    if (_clientConfigCM)
//    {
//        _clientConfigCM->subscribe(prxStr);
//    }
//
//    if (_agentConfigCM)
//    {
//        return _agentConfigCM->getConfig();
//    }

    return ::Ice::StringSeq();
}

::Ice::StringSeq AgentI::subscribeSubscriber(const ::Ice::StringSeq& categories,
        const std::string& prxStr, const ::Ice::Current& current)
{
//    if (_dispatcherConfigCM)
//    {
//        _dispatcherConfigCM->subscribe(categories, prxStr);
//    }

//    if (_agentConfigCM)
//    {
//        return _agentConfigCM->getConfig();
//    }

    return ::Ice::StringSeq();
}

void SendWorker::add(const slice::LogDataSeq& data)
{
    ::IceUtil::Monitor<IceUtil::Mutex>::Lock lock(_dataMutex);
     if(_data.size() == 10000)
     {
        std::cout << " cache data count : 10000 over memory limit! " << std::endl;
     }

    _data.insert(_data.end(), data.begin(), data.end());
    _dataMutex.notify();
}

void SendWorker::run()
{
    std::vector<slice::LogData>::iterator it;
    for (;;)
    {
        ::IceUtil::Monitor<IceUtil::Mutex>::Lock lock(_dataMutex);
        if (_data.empty())
        {
          _dataMutex.wait();
        }
            
        it=_data.begin();
        while(!send(*it))
        {
          std::cout << " fail to send data !" << std::endl;
          sleep(2);
        }
        _data.erase(it);
    }
}

bool NormalSendWorker::send(const slice::LogData& data)
{
    return _adapter_dispatcher->sendNormal(data);
}

bool FailedSendWorker::send(const slice::LogDataSeq& data)
{
    //TODO
    return true;
}

void WhiteListManager::initialize(const std::string& file_name)
{
   wl_file_name=file_name;
}

WhiteListManager::WhiteListManager()
{
}

void WhiteListManager::load()
{
   ::IceUtil::Monitor<IceUtil::Mutex>::Lock lock(_mutex);
   std::ifstream ifs;
   std::string category;

   Ice::StringSeq tmp_white_list;
   ifs.open(wl_file_name.c_str());
   if(ifs.is_open())
   {
      while(!ifs.eof())
      {
         getline(ifs,category,'\n');
         tmp_white_list.push_back(category);
      }
   }
   white_list.swap(tmp_white_list);

   ifs.close();
}

bool WhiteListManager::doValidate(const slice::LogDataSeq& data)
{
   ::IceUtil::Monitor<IceUtil::Mutex>::Lock lock(_mutex);
   slice::LogDataSeq::const_iterator ld_it;
   for(ld_it = data.begin();ld_it != data.end(); ld_it++)
   {
     Ice::StringSeq::const_iterator c_it;
     bool flag=false;
     std::string category;
     int pos=0;
     for(c_it=(*ld_it).categories.begin();c_it!=(*ld_it).categories.end();c_it++)
     {
       if(pos==0)
       {
         if(*c_it == "test")
         {
            flag=true;
            break;
         }
         pos=1;
       }else
       {
         category.append("/");
       }
       category.append(*c_it);
     }
     if(flag)
     {
        continue;
     }
     Ice::StringSeq::iterator w_l_it;
     for(w_l_it=white_list.begin();w_l_it!=white_list.end();w_l_it++)
     {
       if(*w_l_it==category)
       {
         flag=true;
         break;
       }
     }
     if(!flag){
       std::cout << "AgentI::add LogData "<<__LINE__ <<".It does not support categories "<< category << std::endl;
       return false;
     }
   }
   return true;
}

void WhiteListManager::run()
{
    long time=0;
    long tmp;
    for (;;)
    {
       struct stat info;
       stat(wl_file_name.c_str(),&info);
       tmp=info.st_mtime;
       if(time != tmp)
       {
         std::cout<< "WhiteListManager::run(),white list has changed!"<<std::endl;
         time=tmp;
         load();
       }

       sleep(60);
    }
}


}
