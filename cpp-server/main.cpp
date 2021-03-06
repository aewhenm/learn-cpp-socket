#include <iostream>
#include <unistd.h>
#include <netinet/in.h>
#include <cstring>

void error(const char *msg) {
    perror(msg);
    exit(1);
}

const int PORT = 20023;
int connect_sockfd; // connect socket address length
int msg_sockfd;     // message socket address length

int main() {
    printf("Starting server... \n");
    struct sockaddr_in server_addr, client_addr;
    socklen_t clilen = sizeof(client_addr);;

    printf("Opening connection socket... \n");
    connect_sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (connect_sockfd < 0) {
        error("ERROR Couldn't open socket");
    }

    bzero((char *) &server_addr, sizeof(server_addr));
    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = INADDR_ANY;
    server_addr.sin_port = htons(PORT);

    printf("Binding... \n");
    if (bind(connect_sockfd, (struct sockaddr *) &server_addr, sizeof(server_addr)) < 0) {
        error("ERROR Couldn't bind");
    }

    printf("Listening to connection on port %d... \n", PORT);
    listen(connect_sockfd, 5);

    printf("Waiting for an accept... \n");
    msg_sockfd = accept(connect_sockfd, (struct sockaddr *) &client_addr, &clilen);
    if (msg_sockfd < 0) {
        error("ERROR on accept");
    }

    int msg_length = 4096;

    printf("Greeting... \n");
    char s_buff[msg_length];
    std::string msg = "Hello, world from server!";
    s_buff[0] = 's';
    s_buff[1] = msg.length();
    for (int i = 0; i < msg.length(); i++) {
        s_buff[i + 2] = msg[i];
    }
    send(msg_sockfd, s_buff, sizeof(s_buff), 0);

    long readN;
    char buffer[msg_length];
    while ((readN = read(msg_sockfd, buffer, sizeof(buffer))) > 0) {
        char type = buffer[0];
        char length = buffer[1];
        if (type == '0') {
            printf("EXIT message from client! Shutting down...\n");
            break;
        } else if (type == 's') {
            if(length < 1) {
                printf("Message length %d, continuing...", length);
                continue;
            }
            std::string msg = std::string(buffer, 2, length);
            printf("Message from socket: %s\n", msg.c_str());
        }
        bzero(buffer, msg_length);
    }
    if (readN < 0) {
        error("ERROR reading from socket");
    }
    close(msg_sockfd);
    close(connect_sockfd);

    return 0;
}