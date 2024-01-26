#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>

#define PORT 12345
#define MAX_CLIENTS 2

void error(char *msg) {
    perror(msg);
    exit(EXIT_FAILURE);
}

int main() {
    int server_socket, client_sockets[MAX_CLIENTS], client_count = 0;
    struct sockaddr_in server_addr, client_addr;
    socklen_t client_len = sizeof(client_addr);

    // Create socket
    server_socket = socket(AF_INET, SOCK_STREAM, 0);
    if (server_socket == -1)
        error("Error creating server socket");

    // Setup server address structure
    memset(&server_addr, 0, sizeof(server_addr));
    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = INADDR_ANY;
    server_addr.sin_port = htons(PORT);

    // Bind the socket
    if (bind(server_socket, (struct sockaddr*)&server_addr, sizeof(server_addr)) == -1)
        error("Error binding socket");

    // Listen for incoming connections
    if (listen(server_socket, MAX_CLIENTS) == -1)
        error("Error listening for connections");

    printf("Server listening on port %d...\n", PORT);

    // Accept client connections
    while (client_count < MAX_CLIENTS) {
        client_sockets[client_count] = accept(server_socket, (struct sockaddr*)&client_addr, &client_len);
        if (client_sockets[client_count] == -1)
            error("Error accepting connection");

        printf("Client %d connected\n", client_count + 1);
        client_count++;
    }

    // Close the server socket
    close(server_socket);

    return 0;
}
