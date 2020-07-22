from socket import *

serverPort = 12000

# Create server socket, IPv4, UDP
serverSocket = socket(AF_INET, SOCK_DGRAM)

# Binds (assigns) the port number 12000 to the server's socket. So when someone
# sends a packet to serverIP:12000, the packet would be directed to the socket. 
serverSocket.bind(('', serverPort))

print("Server ready to receive on port 12000")

while True:
    message, clientAddress = serverSocket.recvfrom(2048)
    modifiedMessage = message.decode().upper()
    serverSocket.sendto(modifiedMessage.encode(), clientAddress)