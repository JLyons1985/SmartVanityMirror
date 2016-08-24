#############################
## TCP Server that allows us to talk to the java GUI
## for Rachel's vanity mirror
#############################

### Imports
import socketserver
import sys
from TCPRequestHandler import *

HOST = 'localhost'
PORT = 9999

# Create the TCP Socket
try:
    server = socketserver.TCPServer((HOST, PORT), MyTCPHandler)
    print("Socket server created.")
except:
    print("Failed to create socket server.")
    sys.exit()

# Serve until shutdown
server.serve_forever()
    
