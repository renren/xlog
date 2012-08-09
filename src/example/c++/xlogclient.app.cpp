#include "xlog.h"
#include "src/client/client.h"

#include <string>
#include <iostream>
#include <fstream>
#include <boost/lexical_cast.hpp>
#include <boost/algorithm/string/split.hpp>
#include <boost/algorithm/string/classification.hpp>

using namespace std;

int main(int argc , char** argv)
{
    if(argc!=4)
    {
        cout << "Usage: host_1:port_1,...,host_n:port_n  file_name_1,......,filen_name_n categories" << endl;
        return 0;
    }
    int commit_size=100;
    *argv++;
    Ice::StringSeq agent_seq;
    boost::algorithm::split(agent_seq , *argv, boost::algorithm::is_any_of(","));
    *argv++; 
    Ice::StringSeq file_name_seq;
    boost::algorithm::split(file_name_seq , *argv, boost::algorithm::is_any_of(","));
    *argv++;
    Ice::StringSeq categories_seq;
    boost::algorithm::split(categories_seq , *argv, boost::algorithm::is_any_of(","));    
    xlog::Client client(agent_seq); 
    
    Ice::StringSeq::iterator it;
    fstream fs;
    string log;
    Ice::StringSeq buffer;

    int total=0;
    for(it=file_name_seq.begin();it!=file_name_seq.end();it++)
    {
       fs.open(it->c_str());
       if(fs.is_open())
       { 
          int count=0;
          while(!fs.eof())
          {
             count++;
             getline(fs,log,'\n');
             total=total+log.size();
             buffer.push_back(log);
             if(count==commit_size){
                count=0;

                xlog::slice::LogDataSeq log_data_seq;
                xlog::slice::LogData log_data; 
                Ice::StringSeq logs;
                logs.swap(buffer);
	        log_data.categories=categories_seq;
                log_data.logs=logs;
                log_data_seq.push_back(log_data);
               
               while(!client.doSend(log_data_seq))
               {
                  sleep(1);
               }
             }
          }
          if(count>0){
             xlog::slice::LogDataSeq log_data_seq;
             xlog::slice::LogData log_data;
             Ice::StringSeq logs;
             logs.swap(buffer);
             log_data.categories=categories_seq;
             log_data.logs=logs;
             log_data_seq.push_back(log_data);

             while(!client.doSend(log_data_seq))
             {
                sleep(1);
             }
          }
       }else
       {
          cerr << "Fail to open file:" << *it << endl;
       }
    }
    cout << "size:"<< total << endl;
    client.close(); 
    return 0;
}

