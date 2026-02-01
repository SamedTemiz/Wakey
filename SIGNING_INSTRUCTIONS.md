# App Signing Instructions

## For Play Store Release

### Step 1: Generate Release Keystore

```bash
keytool -genkey -v -keystore wakeup-orelse-release.jks \
  -alias wakeup-release \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

**IMPORTANT:**
- Store the keystore file in a SECURE location (NOT in git)
- Remember your keystore password and key password
- Keep a backup of the keystore file - you CANNOT recover it if lost!

### Step 2: Create keystore.properties

Create `keystore.properties` in `/app` directory (NOT committed to git):

```properties
storeFile=path/to/wakeup-orelse-release.jks
storePassword=YOUR_KEYSTORE_PASSWORD
keyAlias=wakeup-release
keyPassword=YOUR_KEY_PASSWORD
```

Add to `.gitignore`:
```
keystore.properties
*.jks
*.keystore
```

### Step 3: Update build.gradle.kts

Uncomment and update the signing config in `app/build.gradle.kts`:

```kotlin
signingConfigs {
    create("release") {
        val keystorePropertiesFile = rootProject.file("keystore.properties")
        val keystoreProperties = Properties()
        keystoreProperties.load(FileInputStream(keystorePropertiesFile))
        
        storeFile = file(keystoreProperties["storeFile"] as String)
        storePassword = keystoreProperties["storePassword"] as String
        keyAlias = keystoreProperties["keyAlias"] as String
        keyPassword = keystoreProperties["keyPassword"] as String
    }
}

buildTypes {
    release {
        signingConfig = signingConfigs.getByName("release") // Update this line
        // ... rest of config
    }
}
```

### Step 4: Build Release APK/AAB

```bash
# For APK
./gradlew assembleRelease

# For Play Store (use AAB)
./gradlew bundleRelease
```

Output location:
- APK: `app/build/outputs/apk/release/app-release.apk`
- AAB: `app/build/outputs/bundle/release/app-release.aab`

### Step 5: Test Release Build

```bash
# Install release APK
adb install app/build/outputs/apk/release/app-release.apk

# Test all alarm scenarios
# Verify ProGuard didn't break anything
```

## Play App Signing (Recommended)

Google recommends using Play App Signing:

1. Upload your first AAB to Play Console
2. Enroll in Play App Signing
3. Google manages the app signing key
4. You keep the upload key (the one you generated above)

**Benefits:**
- Google secures your app signing key
- Can reset upload key if compromised
- Smaller downloads via APK splits

## Security Checklist

- [ ] Keystore file is NOT in version control
- [ ] Keystore backup stored securely (encrypted cloud storage)
- [ ] Passwords are strong and stored securely (password manager)
- [ ] `.gitignore` includes `*.jks` and `keystore.properties`
- [ ] Only share keystore via secure channel if needed

## Troubleshooting

**Error: "Keystore was tampered with, or password was incorrect"**
- Check your password
- Verify keystore file is not corrupted

**Error: "Failed to read key from keystore"**
- Ensure key alias is correct
- Verify key password (may be different from store password)

**ProGuard breaks the app**
- Check `proguard-rules.pro`
- Add keep rules for classes that use reflection
- Test release build thoroughly
