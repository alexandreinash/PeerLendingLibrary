# Step-by-Step Guide: Deploy Peer Lending Library to Render

Follow these steps exactly to deploy your application to Render.

## Prerequisites Checklist

- [ ] Your code is pushed to a GitHub repository
- [ ] You have a Render account (sign up at https://render.com if needed)
- [ ] You have access to your GitHub repository

---

## Step 1: Prepare Your Repository

### 1.1 Verify Files Are Committed

Open your terminal in the project root and run:

```bash
git status
```

Make sure all files are committed. If not, commit them:

```bash
git add .
git commit -m "Prepare for Render deployment"
```

### 1.2 Push to GitHub

```bash
git push origin main
```

(Replace `main` with your branch name if different)

---

## Step 2: Create Render Account & Dashboard

### 2.1 Sign Up / Log In

1. Go to https://render.com
2. Click "Get Started" or "Log In"
3. Sign up with GitHub (recommended) or email

### 2.2 Access Dashboard

After logging in, you'll see the Render Dashboard at https://dashboard.render.com

---

## Step 3: Deploy Using Blueprint (Easiest Method)

### 3.1 Create New Blueprint

1. In Render Dashboard, click the **"New +"** button (top right)
2. Select **"Blueprint"** from the dropdown menu

### 3.2 Connect GitHub Repository

1. Click **"Connect account"** if you haven't connected GitHub yet
2. Authorize Render to access your repositories
3. Search for and select your **PeerLendingLibrary** repository
4. Click **"Connect"**

### 3.3 Review Blueprint Configuration

1. Render will automatically detect `render.yaml` in your repository
2. You'll see a preview of what will be created:
   - **peerlending-backend** (Web Service)
   - **peerlending-frontend** (Static Site)
   - **peerlending-db** (MySQL Database)
3. Review the configuration
4. Click **"Apply"** to start deployment

### 3.4 Wait for Initial Deployment

- Render will start building all services
- This takes 5-10 minutes
- You can watch the build logs in real-time
- **Don't close the browser tab!**

---

## Step 4: Configure Database Connection

### 4.1 Find Your Database Service

1. In Render Dashboard, find **"peerlending-db"** in your services list
2. Click on it to open the database dashboard

### 4.2 Get Connection Details

1. Click on the **"Connections"** tab
2. You'll see connection information like:
   ```
   Internal Database URL: mysql://user:password@hostname:3306/database
   ```
3. **Copy this URL** - you'll need it

### 4.3 Convert to JDBC Format

The URL from Render looks like:
```
mysql://user:password@dorem-1234.render.com:3306/peerlending
```

Convert it to JDBC format:
```
jdbc:mysql://dorem-1234.render.com:3306/peerlending?useSSL=true&serverTimezone=UTC
```

**Extract these parts:**
- **Host**: `dorem-1234.render.com` (part after `@` and before `:`)
- **Port**: `3306` (usually 3306)
- **Database**: `peerlending` (part after last `/`)
- **Username**: `user` (part after `mysql://` and before `:`)
- **Password**: `password` (part after username `:` and before `@`)

---

## Step 5: Configure Backend Service

### 5.1 Open Backend Service

1. In Render Dashboard, find **"peerlending-backend"**
2. Click on it to open the service dashboard

### 5.2 Go to Environment Tab

1. Click on **"Environment"** in the left sidebar
2. You'll see existing environment variables

### 5.3 Set Database Connection Variables

Add or update these variables:

**SPRING_DATASOURCE_URL**
- Click **"Add Environment Variable"**
- Key: `SPRING_DATASOURCE_URL`
- Value: `jdbc:mysql://YOUR_HOST:3306/peerlending?useSSL=true&serverTimezone=UTC`
  - Replace `YOUR_HOST` with the host from Step 4.3
- Click **"Save Changes"**

**SPRING_DATASOURCE_USERNAME**
- Key: `SPRING_DATASOURCE_USERNAME`
- Value: Your database username (from Step 4.3)
- Click **"Save Changes"**

**SPRING_DATASOURCE_PASSWORD**
- Key: `SPRING_DATASOURCE_PASSWORD`
- Value: Your database password (from Step 4.3)
- Click **"Save Changes"**

### 5.4 Set JWT Secret

1. Generate a strong random string (use https://www.random.org/strings/)
   - Length: At least 32 characters
   - Example: `aB3$kL9mN2pQ5rS7tU1vW4xY6zA8bC0dE`
2. Add environment variable:
   - Key: `APP_JWT_SECRET`
   - Value: Your generated secret
   - Click **"Save Changes"**

### 5.5 Set CORS (After Frontend is Deployed)

**Wait until Step 6 is complete**, then:

1. Get your frontend URL (e.g., `https://peerlending-frontend.onrender.com`)
2. Add environment variable:
   - Key: `CORS_ALLOWED_ORIGINS`
   - Value: `https://peerlending-frontend.onrender.com`
     - Replace with your actual frontend URL
   - Click **"Save Changes"**

### 5.6 Verify All Environment Variables

Make sure these are set:
- ✅ `SPRING_PROFILES_ACTIVE` = `production`
- ✅ `SPRING_DATASOURCE_URL` = `jdbc:mysql://...`
- ✅ `SPRING_DATASOURCE_USERNAME` = (your username)
- ✅ `SPRING_DATASOURCE_PASSWORD` = (your password)
- ✅ `APP_JWT_SECRET` = (your secret)
- ✅ `APP_JWT_EXPIRATION_MS` = `86400000`
- ✅ `CORS_ALLOWED_ORIGINS` = (your frontend URL)

### 5.7 Redeploy Backend

1. Go to **"Manual Deploy"** in the left sidebar
2. Click **"Deploy latest commit"**
3. Wait for deployment to complete (2-5 minutes)
4. Check the **"Logs"** tab to ensure it started successfully

---

## Step 6: Configure Frontend Service

### 6.1 Open Frontend Service

1. In Render Dashboard, find **"peerlending-frontend"**
2. Click on it to open the service dashboard

### 6.2 Get Backend URL

1. Go back to **"peerlending-backend"** service
2. Copy the service URL (e.g., `https://peerlending-backend.onrender.com`)
3. Your API base will be: `https://peerlending-backend.onrender.com/api`

### 6.3 Set Environment Variable

1. In frontend service, go to **"Environment"** tab
2. Add environment variable:
   - Key: `REACT_APP_API_BASE`
   - Value: `https://peerlending-backend.onrender.com/api`
     - Replace with your actual backend URL
   - Click **"Save Changes"**

### 6.4 Redeploy Frontend

1. Go to **"Manual Deploy"** in the left sidebar
2. Click **"Deploy latest commit"**
3. Wait for deployment to complete (2-5 minutes)

---

## Step 7: Final Configuration

### 7.1 Update Backend CORS

1. Go back to **"peerlending-backend"** service
2. Go to **"Environment"** tab
3. Update `CORS_ALLOWED_ORIGINS` with your frontend URL:
   - Value: `https://peerlending-frontend.onrender.com`
4. Click **"Save Changes"**
5. Redeploy backend (Manual Deploy → Deploy latest commit)

### 7.2 Test Your Application

1. Open your frontend URL in a browser
2. Try to:
   - Register a new user
   - Log in
   - Browse books
   - Make a request

### 7.3 Check Logs if Issues

If something doesn't work:

1. **Backend logs**: Go to backend service → "Logs" tab
2. **Frontend logs**: Go to frontend service → "Logs" tab
3. **Database logs**: Go to database service → "Logs" tab

Common issues:
- **CORS errors**: Verify `CORS_ALLOWED_ORIGINS` matches frontend URL exactly
- **Database connection**: Verify JDBC connection string format
- **404 errors**: Check that API base URL is correct

---

## Step 8: Verify Everything Works

### 8.1 Test Checklist

- [ ] Frontend loads without errors
- [ ] Can register a new user
- [ ] Can log in with registered user
- [ ] Can view books
- [ ] Can make a borrow request
- [ ] No CORS errors in browser console
- [ ] Backend logs show successful requests

### 8.2 Get Your URLs

**Frontend URL**: `https://peerlending-frontend.onrender.com`
- This is your main application URL

**Backend API URL**: `https://peerlending-backend.onrender.com`
- API endpoints: `https://peerlending-backend.onrender.com/api/...`
- Swagger UI: `https://peerlending-backend.onrender.com/swagger-ui.html`

---

## Troubleshooting

### Backend Won't Start

1. Check **Logs** tab in backend service
2. Common issues:
   - Database connection string format wrong
   - Missing environment variables
   - Port conflict (should use `$PORT` automatically)

### CORS Errors

1. Verify `CORS_ALLOWED_ORIGINS` in backend matches frontend URL exactly
2. No trailing slashes
3. Use HTTPS if backend uses HTTPS
4. Check browser console for exact error

### Database Connection Fails

1. Verify connection string is in JDBC format
2. Check database is running (status should be "Available")
3. Verify username and password are correct
4. Try using Internal Database URL format if external doesn't work

### Frontend Can't Reach Backend

1. Verify `REACT_APP_API_BASE` is set correctly
2. Check backend service is "Live"
3. Test backend URL directly in browser
4. Check browser console for network errors

---

## Next Steps

- [ ] Set up custom domain (optional, paid feature)
- [ ] Configure email notifications (if needed)
- [ ] Set up monitoring and alerts
- [ ] Review security settings
- [ ] Consider upgrading from free tier for production

---

## Quick Reference: Your Service URLs

After deployment, note these URLs:

- **Frontend**: `https://peerlending-frontend.onrender.com`
- **Backend**: `https://peerlending-backend.onrender.com`
- **API Base**: `https://peerlending-backend.onrender.com/api`
- **Swagger**: `https://peerlending-backend.onrender.com/swagger-ui.html`

---

## Support

- **Render Docs**: https://render.com/docs
- **Render Status**: https://status.render.com
- **Render Community**: https://community.render.com

