# SMS Forwarder (personal sideload)

Small Kotlin app: when this phone receives an SMS, it forwards it to a number you set.

Forwarded text looks like:

```text
[Fwd] From +15551212: your message here
```

## Requirements

- Android Studio Ladybug+ (or recent stable)
- A physical phone (SMS APIs are unreliable on emulators)
- Your own device / consent to forward its SMS

## Build & install

1. Open the `sms-forwarder` folder in Android Studio
2. Let Gradle sync
3. Connect your phone with USB debugging enabled
4. Run the **app** configuration (or **Build → Build Bundle(s) / APK(s) → Build APK(s)** and sideload)

On the phone:

1. Open **SMS Forwarder**
2. Tap **Grant SMS permissions** and allow all
3. Enter destination number (include country code, e.g. `+1...`)
4. Tap **Save number**
5. Turn **Enable forwarding** on

## Notes

- Each forward is a normal outbound SMS (carrier charges may apply)
- Messages already starting with `[Fwd]` are not re-forwarded (loop guard)
- SMS from the destination number is not forwarded
- Not meant for Play Store (SMS permission policy)
- Some OEMs kill background apps; if forwarding stops, disable battery optimization for this app

## Project layout

- `MainActivity` — save target number, toggle, request permissions
- `SmsReceiver` — listens for `SMS_RECEIVED` and sends via `SmsManager`
- `Prefs` — SharedPreferences for enabled flag + target number
