# PharmaZen
To address the issue this project adds an special feature: tags the sensitive medicine, if someone wants to buy a restricted medicine they but they can only buy if a Doctor authorized it. There is a time that the authorization is valid. So here is the full go through of the features and setup of my 2nd year's project *PharmaZen* 
# Let's integrate the project according to your need:
1. Create a Maven based JavaFX project in IntelliJ.
2. delete `module-info.java` 
3. Replace all the files and folders of it with the ones in this repository.  
4. Go to [Firebase Console](https://console.firebase.google.com/) and create a Firebase Project for yourself.  
5. You will see a **Settings** button at the top left corner. Go to **Project Settings** and from **General** copy the **Web API Key**.  
6. Paste the key into your source code. Navigate to `FirebaseAuthClient.java` and replace the value of the `API_KEY` string with your key.  
7. Now go to your **Project Overview**, select **Authentication**, and in the **Sign-in method** tab enable **Email/Password**.  
8. Then again, go to your **Project Overview** and select **Firestore Database**.  
9. Click **Create database**, choose the appropriate security rules (start in test mode if you want to develop quickly, but secure your rules before production), and select a Cloud Firestore location near you.  
10. Download your Firebase service account JSON file from **Project Settings > Service accounts** and save it in the path `src/main/resources/serviceAccountKey_PharmaZen.json`.  
11. Make sure your JavaFX application calls the Firebase initialization method (`FireBaseInit.FirebaseInit()`) early in the startup process to properly initialize Firebase SDK.  
12. Finally, navigate to `EmailService.java`, you will see two variables named `String from = ""` & `String password = "";`.  
13. You need to fill the `from` String with your personal Gmail (make sure 2FA is enabled for the Gmail account you are using), e.g., `String from = "zishahed25@gmail.com"`.  
14. To fill the password, go to [Google App Passwords](https://myaccount.google.com/apppasswords).  
15. Now enter an app name, e.g., "practice", and after creating it, it will give you a password. Copy it and paste it into the `password` String.  
16. You are good to go now!
# Note
  1. This project uses Firebase Authentication REST API to securely sign in users via email/password.
  2. OTPs are generated and stored in Firestore and sent via SMTP (Gmail SMTP is used by default).
  3. Make sure to update your SMTP credentials in EmailService.java to send emails correctly.
  4. For production, never store plaintext passwords in Firestore. This project stores passwords only for demonstration purposes.
  5. Use Firebase Authentication for secure password management.
  6. Customize Firebase Authentication email templates in Firebase Console to match your app branding.
# Set Edit Configuration as Module-info.java is deleted 
  1. Go to Run -> Edit Configuration in IntelliJ
  2. Click on the `+` icon. and select Maven.
  3. in Run dialog write `javafx:run`
  4. In Working Directory, select your current project (ususally the articafact name you set while create the project).
  5. In Name, give any Name.
  6. Now select Apply and Ok.
  7. You will see the configuration in the upper middle portion of your Java Project beside Play button instead of `Current File`.
  8. That it!
# ScreenShots
## Login Page 
![Screenshot of PharmaZen-LoginPage](https://github.com/user-attachments/assets/8a5b2638-3f51-41ea-acf8-62d5c1e529b8)
## Registration Page
![Screenshot of PharmaZen-RegistrationPage](https://github.com/user-attachments/assets/042720d5-5648-43f3-a288-9f5386e94b0a)
## Dashboard (Other profession)
![Screenshot of PharmaZen-Dashboard](https://github.com/user-attachments/assets/2bb66f28-72e1-42a3-9e89-a93d283e554c)
## 2FAVerfication
![Screenshot of PharmaZen-Verification](https://github.com/user-attachments/assets/913c8e11-b91d-4334-a90e-1bfb1fc2563e)
## Dashboard for Authorization (Only for Doctors)
![Screnshot of PharmaZen-Dashboard-Authorization](https://github.com/user-attachments/assets/a3aeb685-41d9-42ac-8ca2-9828eaddb4e5)

# Appreciations
Of course to chatgpt for making those .css files for me as I got not expertise in that field up until to this day. Used the database of Bangladeshi Medicine from [Kaggle](https://www.kaggle.com/) and to [Ajwad Mohtasim](https://github.com/ajwadmohtasim) cleaned those database according to my need.
