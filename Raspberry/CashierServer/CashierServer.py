import os
import sys
import json
import traceback
import pyrebase
import schedule
import time
import json
from datetime import datetime
import calendar
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
            
    @cherrypy.expose
    
    def movetohistory(self, cashier):
        answer = {'state': 'ok'}
        cherrypy.log(cashier)

        # configuration object
        firebaseConfig = {
            "apiKey": "AIzaSyBHxxbaR8a0HidckGOUZDuxU3qh7SN_kws",
            "authDomain": "weightingcashierse.firebaseapp.com",
            "databaseURL": "https://weightingcashierse-default-rtdb.europe-west1.firebasedatabase.app",
            "projectId": "weightingcashierse",
            "storageBucket": "weightingcashierse.appspot.com",
            "messagingSenderId": "791363006189",
            "appId": "1:791363006189:web:9dfbea915bceccab83e6e4"
        }

        # firebase connection
        firebase = pyrebase.initialize_app(firebaseConfig)

        # firebase database
        db = firebase.database()
        # Cahiers
        cash = db.child("Cashiers").child(cashier).get()

        # getting unixtime
        d = datetime.utcnow()
        unixtime = calendar.timegm(d.utctimetuple())

        data = {
            unixtime: cash.val()
            
        }

        # History
        history = db.child("History").child(cashier).get()
        historyjson = history.val()
        historyjson.update(data)

        #y = json.dumps(jsonload)
        #cherrypy.log(y)

        result = db.child("History").child(cashier).set(historyjson)
        result = db.child("Cashiers").child(cashier).set(json.loads('{"null": "null"}'))

        cherrypy.response.headers['Cache-Control'] = 'no-cache, no-store'
        cherrypy.response.headers['Pragma'] = 'no-cache'
        cherrypy.response.headers['Content-Type'] = 'application/json'
        return json.dumps(answer).encode('utf-8')
        



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
