import os
import sys
import json
import traceback
from collections import OrderedDict
from threading import Lock

import cherrypy


class ConfigNotValidException(Exception):
    pass

class CashierServer:

    @cherrypy.expose
    def index(self):
        with open(os.path.join('www', 'index.html'), 'r') as file:
            content = file.read()
            return content


if __name__ == '__main__':

    # Cherrypy config
    conf = {
        '/': {
            'tools.sessions.on': False,
            'tools.staticdir.root': os.path.abspath(os.getcwd() + '/www')
        },
        'global': {
            'engine.autoreload.on': False,
            'server.socket_host': '0.0.0.0',
            'server.socket_port': 8080
        },
        '/static': {
            'tools.staticdir.on': True,
            'tools.staticdir.dir': './static'
        },
        '/lib': {
            'tools.staticdir.on': True,
            'tools.staticdir.dir': './lib'
        },
        '/images': {
            'tools.staticdir.on': True,
            'tools.staticdir.dir': './images'
        },
        '/favicon.ico': {
            'tools.staticfile.on': False
        }
    }

    # Start webserver
    cherrypy.quickstart(CashierServer(), '/', conf)
