#!/bin/bash
#
# install.sh
# Install script
#

# print python version
echo "python version before installation"
python -V

# check for updates
echo "run apt update"
sudo apt update

# install utilities
echo "install utilities"
sudo apt-get install -y build-essential tk-dev libncurses5-dev libncursesw5-dev libreadline6-dev libdb5.3-dev libgdbm-dev libsqlite3-dev libssl-dev libbz2-dev libexpat1-dev liblzma-dev zlib1g-dev libffi-dev tar wget vim

# download python
echo "download python"
wget https://www.python.org/ftp/python/3.8.0/Python-3.8.0.tgz

# install python
echo "install python"
echo "uncompress python package"
sudo tar zxf Python-3.8.0.tgz

echo "change directory"  
cd Python-3.8.0

echo "running optimizations"
sudo ./configure --enable-optimizations

echo "make -j 4"
sudo make -j 4

echo "make altinstall"
sudo make altinstall

# print python version
echo "python3.8 version"
python3.8 -V

# install cherrypy
echo "installing cherrypy"
sudo python3.8 -m pip install cherrypy

