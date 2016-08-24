#############################
## TCP Handler that allows us to talk to the java GUI
## for Rachel's vanity mirror
#############################

import socketserver
import json

class MyTCPHandler(socketserver.StreamRequestHandler):

    def handle(self):
        while 1:
            incomingString = str(self.rfile.readline(), "utf-8")
            myData = json.loads(incomingString)['map']

            print(incomingString)
            print(myData)

            if myData['messageType'] == 'Request':
                if myData['message'] == 'SomeRequest':
                    ResponseStr = "{'map':{'messageType': 'Return', 'Return': {$

            self.wfile.write(bytes(ResponseStr, 'utf-8'))

    
