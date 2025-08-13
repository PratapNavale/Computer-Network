#include <iostream>
using namespace std;

int main() {
    int data[7];

    cout << "--- Sender Side ---\n";
    cout << "Enter 4 data bits (0 or 1):\n";
    cout << "Data bit 1: ";
    cin >> data[2];
    cout << "Data bit 2: ";
    cin >> data[4];
    cout << "Data bit 3: ";
    cin >> data[5];
    cout << "Data bit 4: ";
    cin >> data[6];

    data[0] = data[2] ^ data[4] ^ data[6];
    data[1] = data[2] ^ data[5] ^ data[6];
    data[3] = data[4] ^ data[5] ^ data[6];

    cout << "\nGenerated 7-bit Hamming code:\n";
    for (int i = 0; i < 7; i++) {
        cout << data[i] << " ";
    }
    cout << endl;

    int received[7];
    cout << "\n--- Receiver Side ---\n";
    cout << "Enter received 7-bit Hamming code (bit by bit):\n";
    for (int i = 0; i < 7; i++) {
        cout << "Bit " << i + 1 << ": ";
        cin >> received[i];
    }

    int p1 = received[0] ^ received[2] ^ received[4] ^ received[6];
    int p2 = received[1] ^ received[2] ^ received[5] ^ received[6];
    int p3 = received[3] ^ received[4] ^ received[5] ^ received[6];
    int errorPos = p3 * 4 + p2 * 2 + p1;

    if (errorPos == 0) {
        cout << "\nNo error detected in received code.\n";
    } else {
        cout << "\nError detected at position: " << errorPos << endl;
        received[errorPos - 1] ^= 1;
        cout << "Corrected Hamming code:\n";
        for (int i = 0; i < 7; i++) {
            cout << received[i] << " ";
        }
        cout << endl;
    }

    cout << "\nExtracted data bits from receiver:\n";
    cout << received[2] << " " << received[4] << " " << received[5] << " " << received[6] << endl;

    return 0;
}