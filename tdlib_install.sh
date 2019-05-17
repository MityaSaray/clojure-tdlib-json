#!/usr/bin/env bash
sudo apt-get install make git zlib1g-dev libssl-dev gperf php cmake g++
if [ ! -d "td" ]; then
   git clone https://github.com/tdlib/td.git
fi
cd td
if [ ! -d "build" ]; then
   mkdir "build"
fi
cd build
export CXXFLAGS=""
cmake -DCMAKE_BUILD_TYPE=Release -DCMAKE_INSTALL_PREFIX:PATH=../tdlib ..
cmake --build . --target install -- -j 8
cd ..
mkdir ../src/tdlib_json_clojure_wrapper/tg_connector/build
mv -i ./build ../src/tdlib_json_clojure_wrapper/tg_connector/