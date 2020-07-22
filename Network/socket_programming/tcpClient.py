from socket import *

serverName = "127.0.0.1"
serverPort = 12000

# Creates client socket, IPv4, TCP
clientSocket = socket(AF_INET, SOCK_STREAM)

# Establish the TCP connection.
# When return, the three-way handshake is done and connection is establishes.
clientSocket.connect((serverName, serverPort))

# Send to socket, without specifying destination socket address
clientSocket.send(input("Input: ").encode())

# Characters accumulate until line ends with a carriage return character
response = clientSocket.recv(1024).decode()

clientSocket.close()

print("Received:", response)