/*statement: 
Write a program to simulate Go back N and Selective Repeat Modes of
Sliding Window protocol using C/C++/Java


input, output: 
Enter number of frames to send: 4
Choose Protocol:
1. Go-Back-N
2. Selective Repeat
3. Exit
Enter choice: 1

--- Go-Back-N Protocol (Successful Transmission) ---
Sender: Sending Frame 1
Receiver: Received Frame 1
Receiver: Sending ACK for Frame 1
Sender: Sending Frame 2
Receiver: Received Frame 2
Receiver: Sending ACK for Frame 2
Sender: Sending Frame 3
Receiver: Received Frame 3
Receiver: Sending ACK for Frame 3
Sender: Sending Frame 4
Receiver: Received Frame 4
Receiver: Sending ACK for Frame 4

Enter the frame number that was lost (0 if no loss): 2

--- Frame 2 is lost. Go-Back-N Resending ---
Sender: Resending Frame 2
Receiver: Received Frame 2
Receiver: Sending ACK for Frame 2
Sender: Resending Frame 3
Receiver: Received Frame 3
Receiver: Sending ACK for Frame 3
Sender: Resending Frame 4
Receiver: Received Frame 4
Receiver: Sending ACK for Frame 4

Choose Protocol:
1. Go-Back-N
2. Selective Repeat
3. Exit
Enter choice: 2

--- Selective Repeat Protocol (Successful Transmission) ---
Sender: Sending Frame 1
Receiver: Received Frame 1
Receiver: Sending ACK for Frame 1
Sender: Sending Frame 2
Receiver: Received Frame 2
Receiver: Sending ACK for Frame 2
Sender: Sending Frame 3
Receiver: Received Frame 3
Receiver: Sending ACK for Frame 3
Sender: Sending Frame 4
Receiver: Received Frame 4
Receiver: Sending ACK for Frame 4

Enter the frame number that was lost (0 if no loss): 0

All frames transmitted and acknowledged successfully using Selective Repeat.

Choose Protocol:
1. Go-Back-N
2. Selective Repeat
3. Exit
Enter choice: 3
Exiting program.*/


#include <iostream>
using namespace std;

void goBackN(int frames[], int n) {
    cout << "\n--- Go-Back-N Protocol (Successful Transmission) ---\n";
    for (int i = 0; i < n; i++) {
        cout << "Sender: Sending Frame " << frames[i] << endl;
        cout << "Receiver: Received Frame " << frames[i] << endl;
        cout << "Receiver: Sending ACK for Frame " << frames[i] << endl;
    }

    int lost;
    cout << "\nEnter the frame number that was lost (0 if no loss): ";
    cin >> lost;

    if (lost == 0) {
        cout << "\nAll frames transmitted and acknowledged successfully using Go-Back-N.\n";
    } else {
        cout << "\n--- Frame " << lost << " is lost. Go-Back-N Resending ---\n";
        for (int i = lost - 1; i < n; i++) {
            cout << "Sender: Resending Frame " << frames[i] << endl;
            cout << "Receiver: Received Frame " << frames[i] << endl;
            cout << "Receiver: Sending ACK for Frame " << frames[i] << endl;
        }
    }
}

void selectiveRepeat(int frames[], int n) {
    cout << "\n--- Selective Repeat Protocol (Successful Transmission) ---\n";
    for (int i = 0; i < n; i++) {
        cout << "Sender: Sending Frame " << frames[i] << endl;
        cout << "Receiver: Received Frame " << frames[i] << endl;
        cout << "Receiver: Sending ACK for Frame " << frames[i] << endl;
    }

    int lost;
    cout << "\nEnter the frame number that was lost (0 if no loss): ";
    cin >> lost;

    if (lost == 0) {
        cout << "\nAll frames transmitted and acknowledged successfully using Selective Repeat.\n";
    } else {
        cout << "\n--- Frame " << lost << " is lost during transmission ---\n";

        for (int i = 0; i < n; i++) {
            if (frames[i] == lost) {
                cout << "Sender: Sending Frame " << frames[i] << endl;
                cout << "Receiver: Frame " << lost << " not received." << endl;
                cout << "Receiver: Sending NACK for Frame " << lost << endl;
            } else {
                cout << "Sender: Sending Frame " << frames[i] << " again (if needed)" << endl;
                cout << "Receiver: Frame " << frames[i] << " already buffered." << endl;
            }
        }

        cout << "\nSender: Retransmitting only Frame " << lost << " on receiving NACK." << endl;
        cout << "Receiver: Received Frame " << lost << " (retransmitted)" << endl;
        cout << "Receiver: Sending ACK for Frame " << lost << endl;

        cout << "\nAll frames reordered and received successfully using Selective Repeat.\n";
    }
}

int main() {
    int n, choice;
    cout << "Enter number of frames to send: ";
    cin >> n;

    int frames[n];
    for (int i = 0; i < n; i++) {
        frames[i] = i + 1;
    }

    do {
        cout << "\nChoose Protocol:";
        cout << "\n1. Go-Back-N";
        cout << "\n2. Selective Repeat";
        cout << "\n3. Exit";
        cout << "\nEnter choice: ";
        cin >> choice;

        switch (choice) {
            case 1:
                goBackN(frames, n);
                break;
            case 2:
                selectiveRepeat(frames, n);
                break;
            case 3:
                cout << "Exiting program.\n";
                break;
            default:
                cout << "Invalid choice. Please enter 1, 2, or 3.\n";
        }
    } while (choice != 3);

    return 0;
}