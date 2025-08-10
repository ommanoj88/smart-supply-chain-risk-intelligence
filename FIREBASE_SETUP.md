# Firebase Service Account Setup Instructions

To run this application, you need to obtain a Firebase service account key file.

## Steps to get the service account key:

1. **Go to Firebase Console**
   - Visit: https://console.firebase.google.com/
   - Select your project: `rentvat`

2. **Navigate to Service Accounts**
   - Click on the gear icon (Project Settings)
   - Go to the "Service accounts" tab

3. **Generate Private Key**
   - Click "Generate new private key"
   - Confirm by clicking "Generate key"
   - A JSON file will be downloaded

4. **Place the file**
   - Rename the downloaded file to `service-account-key.json`
   - Place it in the root directory of this project (same level as `pom.xml`)

5. **Security Note**
   - This file contains sensitive credentials
   - Never commit it to version control
   - It's already included in `.gitignore`

## Alternative: Using Environment Variables

Instead of a file, you can set the `GOOGLE_APPLICATION_CREDENTIALS` environment variable:

```bash
export GOOGLE_APPLICATION_CREDENTIALS="/path/to/service-account-key.json"
```

## Verification

Once the file is in place, the backend should start successfully without Firebase initialization errors.

## File Format Example

The `service-account-key.json` should look like this:

```json
{
  "type": "service_account",
  "project_id": "rentvat",
  "private_key_id": "...",
  "private_key": "-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n",
  "client_email": "...",
  "client_id": "...",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "...",
  "client_x509_cert_url": "..."
}
```

## Troubleshooting

If you get Firebase initialization errors:
- Check that the file exists and is named correctly
- Verify the JSON is valid
- Ensure the service account has the correct permissions
- Check that the project ID matches your Firebase project