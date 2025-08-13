#include <iostream>
using namespace std;

int main() {
    int totalFrames, windowSize;
    cout << "Enter total number of frames: ";
    cin >> totalFrames;
    cout << "Enter window size: ";
    cin >> windowSize;

    int sentUpto = 0;
    int ack;

    while(sentUpto < totalFrames) {
        cout << "\nSending frames: ";
        for(int i=sentUpto; i<sentUpto + windowSize && i < totalFrames; i++) {
            cout << i << " ";
        }

        cout  << "\nEnter last acknowledgment recieved (-1 if ACK lost): ";
        cin >> ack;

        if (ack == -1) {
            cout << "Acklodgement lost! Resending the frames " << sentUpto << endl;

        } else if (ack >=sentUpto && ack < totalFrames) {
            sentUpto = ack + 1;
            cout << "Window slides. Next frame to send: " << sentUpto << endl;
        } else {
            cout << "Invalid Acknowledgment. Resending from frame " << sentUpto << endl;
        }
    }
    cout << "\nAll frames sent and acknowledgment successfully!\n";
    return 0;
}
