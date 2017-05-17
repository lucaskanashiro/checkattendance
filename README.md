# CheckAttendance

CheckAttendance is a native Android app that allows one to manage seminars in
academic context. One could verify the presence of attendees using two
different mechanisms, using QR code or bluetooth.

## QR code

The teacher/professor/someone that is managing it can see the details of a
seminar and select the QR code verification option, when it is done, a QR code
is shown with seminar information.

The student that is attending this seminar should see the details and select
the QR code verification option as well. The camera will be opened to scan the
teacher's QR code. After seminar's info is scanned the attendance is confirmed.


## Bluetooth

In seminar's details, teacher or the responsible for the seminar should select
Blutooth verification option. If the bluetooth is not enabled, the app will ask
if we can do it for you. After that, your smartphone will be accepting
connections via bluetooth from other devices. It is important to notice that
you need to make your bluetooth device visible to allow others to discover it,
our app just enable it for you. To stop to accept new connections you just need
to return to the previous screen.

Student should select Bluetooth verification option in seminar's details as
well, and choose the bluetooth device to connect with. If the other bluetooth
device (teacher's smartphone) is already paired with yours, you do not need to
scan the area trying to find other devices, just select it. If not, you need to
scan the nearby area, find the target device and click on it. After choose the
target device, you will connect with it and fetch the seminar information to
confirm your attendance.

### Dependencies

All the development and runtime dependencies is managed by gradle, you can
check them on build.gradle script. If you build the project with gradle
everything should work fine. If not, good look to figure it out :)

The project is developed on Android studio, it will be your friend to setup
the entire environment for developemt and test.

### Run tests

As the project has been developed on Android studio, the best way to execute
the suite test is on it :)

Import the project, click with right button on instrument test or unit test
project, and select "Run". In the case of instrument tests you need to have a
device (virtual or not) connected to perform the tests.

### License

This project is under Apache-2.0 license, you can read the entire license in
LICENSE file.
