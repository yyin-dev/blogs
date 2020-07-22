from socket import *

serverName = '127.0.0.1'
serverPort = 12000

# Create the client socket
# AF_INET means we use IPv4, SOCK_DGRAM means this's a UDP socket
# We don't specify the port when creating client socket, we let the OS do it.
clientSocket = socket(AF_INET, SOCK_DGRAM)

# Send the message to server
# sentence.encode() convers the string into bytes.
clientSocket.sendto(input('Input: ').encode(), (serverName, serverPort))

# serverAddr would contains both the server's IP and port number
# 2048 is the buffer size
upperSentence, serverAddress = clientSocket.recvfrom(2048)

print("From {} received: {}".format(serverAddress, upperSentence.decode()))

clientSocket.close()