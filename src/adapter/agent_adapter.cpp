#include <boost/lexical_cast.hpp>

#include "src/common/util.h"
#include "src/adapter/agent_adapter.h"

namespace xlog
{

bool AgentAdapter::init(const ::Ice::StringSeq& defaultAgents,bool is_udp_protocol)
{  
    if (defaultAgents.empty())
    {
        std::cerr << "AgentAdapter::init defaultAgent is empty!" << std::endl;
        return false;
    }

    srand(unsigned(time(NULL)));

    _ic = ::Ice::initialize();

    srand((unsigned) time(NULL));
    current_agent_prx_number=0;

    std::vector<slice::AgentPrx> _prxs;
    for (::Ice::StringSeq::const_iterator it = defaultAgents.begin(); it != defaultAgents.end();
            ++it)
    {
        slice::AgentPrx prx = Util::getPrx<slice::AgentPrx>(_ic, *it, is_udp_protocol, 300);
	_prxs.push_back(prx);
    }
    agent_prxs.swap(_prxs);

    return true;
}

slice::AgentPrx AgentAdapter::getAgentPrx()
{
    int size=agent_prxs.size();
    if(current_agent_prx_number==size)
    {
        current_agent_prx_number=0;
    }
    return agent_prxs.at(current_agent_prx_number++);
    
}

bool AgentAdapter::send(const slice::LogDataSeq& data)
{
    int size=agent_prxs.size();
    for(int i=0;i<size;i++)
    {
       try
       {
           getAgentPrx()->add(data);
           return true;
       } catch (::Ice::Exception& e)
       {
           std::cerr << "AgentAdapter::send failed for " << i+1  << " time, will send again!" << std::endl;
       }
    }
    return false;
}

}
