# How to set up your AWS Account and connect it to your local backend env 
Each team member who wants to run the BE locally, he needs to:

- **Create his own AWS account**.
- **Set up AWS credentials locally** to use the AWS SDK and AWS CLI.
- **Deploy the CloudFormation stack** using the existing template you provided.
- **Use their own AWS resources** during development without interfering with others.

### **1. Creating an AWS Account**

Each team member needs to create their own AWS account. Here's how:

#### **Step 1: Sign Up for an AWS Account**

1. **Go to the AWS Signup Page**:

    - [AWS Signup Page](https://portal.aws.amazon.com/billing/signup)

2. **Click on "Create a new AWS account"**.

3. **Provide Your Email and Password**:

    - **Email Address**: Use your work email or a personal email if preferred.
    - **Password**: Choose a strong password.
    - **AWS Account Name**: Enter a name for your account (e.g., "John Doe Development Account").

4. **Verify Your Email**:

    - AWS will send a verification email. Click the verification link in the email.

5. **Provide Contact Information**:

    - **Account Type**: Select **"Personal"** or **"Business"** as appropriate.
    - **Full Name**: Enter your name.
    - **Phone Number**: Provide a valid phone number.
    - **Country/Region**: USA (East) 
    - **Address**: Enter your address.

6. **Choose an AWS Support Plan**:

    - Select **"Basic Support – Free"**.

7. **Provide Payment Information**:

    - **Credit/Debit Card**: Enter your card details. AWS requires this for verification, but you won't be charged if you stay within the Free Tier limits.
    - **Billing Address**: Confirm your billing address.

8. **Identity Verification**:

    - AWS may require phone verification.
    - Choose **"Text Message (SMS)"** or **"Voice Call"**.
    - Enter the code sent to your phone.

9. **Accept the AWS Customer Agreement**:

    - Read and accept the terms.

10. **Complete Sign-Up**:

    - After completing these steps, AWS will set up your account.
    - You will receive a confirmation email when your account is ready.

#### **Step 2: Log In to the AWS Console**

1. **Go to the AWS Management Console**:

    - [AWS Management Console](https://console.aws.amazon.com/)

2. **Log In Using Your Root Account Credentials**:

    - **Email**: Enter the email address you used to sign up.
    - **Password**: Enter your password.

**Note**: The root account has full access to your AWS resources. It's recommended in production to create an IAM user for daily tasks to enhance security.

---

### **2. Configuring AWS Credentials Using AWS CLI**

Each team member should configure their local environment to interact with their AWS account securely.

#### **Step 1: Create an IAM User**

**It's a best practice not to use root account credentials for programmatic access.**

1. **Navigate to the IAM Console**:

    - [AWS IAM Console](https://console.aws.amazon.com/iam/)

2. **Create a New IAM User**:

    - Click on **"Users"** in the sidebar.
    - Click **"Add user"**.

3. **Set User Details**:

    - **User name**: Choose a username (e.g., "john-doe").
    - **Access type**: Check **"Programmatic access"**.

4. **Set Permissions**:
   1. Click on the **"Add permissions"** button.
   2. Choose **"Attach existing policies directly"**.
   3. **Select the Following Policies**:
       - **AmazonCognitoPowerUser**: Grants full access to Amazon Cognito resources.
       - **AWSCloudFormationFullAccess**: Grants full access to AWS CloudFormation actions, including `DescribeStacks`.

5. **Review**:

    - Confirm the user details and permissions.

6. **Create User**:

    - Click **"Create user"**.

7. **Download Access Keys**:

    - **Access Key ID** and **Secret Access Key** will be displayed.
    - **IMPORTANT**: Download the `.csv` file or copy the keys securely.
    - **Note**: You won't be able to retrieve the secret access key again.

#### **Step 2: Install AWS CLI**

1. **Download and Install AWS CLI**:

    - Follow the instructions in the [AWS CLI Installation Guide](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html) for your operating system.

#### **Step 3: Configure AWS CLI**

1. **Open Your Terminal**.

2. **Run `aws configure` Command**:

   ```bash
   aws configure
   ```

3. **Enter Your IAM User Credentials**:

    - **AWS Access Key ID**: Enter the access key ID from the IAM user you created.
    - **AWS Secret Access Key**: Enter the secret access key.
    - **Default region name**: Enter the region you wish to use (e.g., `us-east-1`).
    - **Default output format**: Optionally enter `json`.

   Example:

   ```bash
   $ aws configure
   AWS Access Key ID [None]: AKIAIOSFODNN7EXAMPLE
   AWS Secret Access Key [None]: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
   Default region name [None]: us-east-1
   Default output format [None]: json
   ```

4. **Verify Configuration**:

    - Run the following command to test if the AWS CLI is configured correctly:

      ```bash
      aws sts get-caller-identity
      ```

    - You should see output similar to:

      ```json
      {
          "UserId": "AIDABCDEFGHIJK123456",
          "Account": "123456789012",
          "Arn": "arn:aws:iam::123456789012:user/john-doe"
      }
      ```

---

### **3. Deploying the CloudFormation Stack Using the Provided Template**

You can now deploy the CloudFormation stack in your own AWS account.


#### **Step 1: Save the CloudFormation Template**

1. **Open Your Terminal**.

2. **Navigate to the devops directory in the project **:

3. **Run the `aws cloudformation deploy` Command**:

   ```bash
   aws cloudformation deploy \
     --template-file aws-user-pool.yaml \
     --stack-name ilim-user-pool-1 \
     --capabilities CAPABILITY_NAMED_IAM
   ```

    - **`--template-file`**: Path to your CloudFormation template.
    - **`--stack-name`**: A name for your CloudFormation stack.
    - **`--capabilities CAPABILITY_NAMED_IAM`**: Required if your template includes IAM resources.

4. **Wait for the Deployment to Complete**:

    - The command will output the progress.
    - Once completed, you can verify the stack in the AWS CloudFormation console.

#### **Step 3: Retrieve Stack Outputs**

After the stack is deployed, you need to retrieve outputs like `UserPoolId` and `UserPoolClientId` to configure your application.

1. **Using AWS CLI**:

   ```bash
   aws cloudformation describe-stacks --stack-name ilim-user-pool-1
   ```
    - Look for the `Outputs` section in the JSON response.

2. **Using AWS Console**:

    - Navigate to the **"Stacks"** page in CloudFormation.
    - Click on your stack (`ilim-user-pool-1`).
    - Click on the **"Outputs"** tab to view the output values.

---

### **4. Configuring Your Application**

#### **Update Application Properties**

In the project, navigates to `src/main/resources/application-local.properties`, set the AWS resources' identifiers:

```properties
aws.cognito.userPoolId=YOUR_USER_POOL_ID_FROM_OUTPUT
aws.cognito.clientId=YOUR_USER_POOL_CLIENT_ID_FROM_OUTPUT
aws.cognito.region=us-east-1  # Or your selected region
```

**Note**: Ensure that the `aws.cognito.region` matches the region where you deployed the stack, which should be us-east-1

### **5. Important Considerations**

#### **AWS Free Tier Usage**

- **Monitor Your Usage**:

    - Keep track of your resource consumption to stay within the Free Tier limits.
    - Use the AWS Billing Dashboard to monitor costs.

- **Understand Free Tier Limits**:

    - **Cognito**: No charge for up to 50,000 monthly active users.
    - **CloudFormation**: No additional charge, but additional resources created may incur costs.

#### **Resource Cleanup**

- **Delete Unused Resources**:

    - To avoid unexpected charges, delete resources when they are no longer needed.
    - You can delete the CloudFormation stack to remove all resources it created.
