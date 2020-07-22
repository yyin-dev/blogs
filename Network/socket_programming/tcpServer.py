from socket import *

serverPort = 12000

# Create welcome socket
serverSocket = socket(AF_INET, SOCK_STREAM)

# Bind to server socket
serverSocket.bind(('', serverPort))

# listens for connection request
# The parameter specifies the maximum number of queued connections (>= 1)
serverSocket.listen(1)

while True:
    # Accepts connection request, creates a dedicated socket, and starts 
    # three-way handshake. When accept() returns, connection is established.
    connectionSocket, addr = serverSocket.accept()

    # Receive from connection
    sentence = connectionSocket.recv(1024).decode()

    modified = sentence.upper()

    # Send response back into socket
    connectionSocket.send(modified.encode())

    # Close the dedicated socket, but the welcome socket remains open
    connectionSocket.close()

