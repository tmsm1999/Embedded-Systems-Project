import pyrebase
import json
import schedule
import time
import os


def sync_data_from_firebase():

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
    cash = db.child("Cashiers").get()
    # History
    hist = db.child("History").get()
    # Products
    prod = db.child("Products").get()

    save_json_file(cash, hist, prod)


def save_json_file(cash, hist, prod):

    dictionary = {}
    mini_dictionary = {}

    for c in cash.each():
        if c.val() != None:
            mini_dictionary[c.key()] = c.val()
    dictionary["Cashier"] = mini_dictionary

    mini_dictionary = {}
    for c in hist.each():
        if c.val() != None:
            mini_dictionary[c.key()] = c.val()
    dictionary["History"] = mini_dictionary

    mini_dictionary = {}
    for c in prod.each():
        if c.val() != None:
            mini_dictionary[c.key()] = c.val()
    dictionary["Products"] = mini_dictionary

    #with open("CashierLocalDB.json", "w") as f:
        #json.dump(dictionary, f, ensure_ascii=False, indent=4)
    f = open("CashierLocalDB.json", "w")
    json.dump(dictionary, f, ensure_ascii=False, indent=4)

    f.flush()
    os.fsync(f.fileno())
    f.close()
    print("Sycronization complete!")

schedule.every(2).seconds.do(sync_data_from_firebase)


while True:
    schedule.run_pending()
    time.sleep(1)
