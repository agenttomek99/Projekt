import json
from flask import Flask
app = Flask(__name__)


import logging
log = logging.getLogger('werkzeug')
log.setLevel(logging.ERROR)

@app.route('/<queue>/<current_length>/<average_length>')
def data(queue, current_length, average_length):
   print(queue, current_length, average_length)
   return "recieved"
app.run()

