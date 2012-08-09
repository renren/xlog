
ifndef ICE_HOME
        ICE_HOME=/data/xlog-client/Ice-3.3
endif
ifndef XLOG_HOME 
	XLOG_HOME=/data/xlog-client/xlog/build
endif

all:build/client.example build/xlogclient.app

clean:
	rm -rf build

build/client.example: client_example.cpp
	mkdir -p build
	g++ -o $@ -I$(XLOG_HOME)/include -I$(ICE_HOME)/include -L$(XLOG_HOME)/lib -L$(ICE_HOME)/lib $^ -lIce -lClient
build/xlogclient.app: xlogclient.app.cpp
	mkdir -p build
	g++ -o $@ -I$(XLOG_HOME)/include -I$(ICE_HOME)/include -L$(XLOG_HOME)/lib -L$(ICE_HOME)/lib $^ -lIce -lClient

