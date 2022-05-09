package ua.com.cruises.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import ua.com.cruises.controller.uris.AppPath;
import ua.com.cruises.model.User;
import ua.com.cruises.repository.UserDao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@WebServlet(AppPath.PATH_TO_USER_PHOTO_UPLOAD_PAGE)
@MultipartConfig
public class UserImagesUpload extends HttpServlet {
    private final UserDao userDao = UserDao.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        final String PATH = getServletContext().getInitParameter("upload.location") + "/" + ((User)req.getSession().getAttribute("user")).getUsername();
        User user = (User) req.getSession().getAttribute("user");

        List<String> fileNames = new ArrayList<>();
        for (Part part : req.getParts())
            fileNames.add(String.format("/%s/%s", user.getUsername(), part.getSubmittedFileName()));

        if (fileNames.stream().allMatch(file -> file.matches(".*[.][pP][nN][gG]$")) && userDao.insertUserImagesById(user.getId(), fileNames)) {
            for (Part part : req.getParts()) {
                if (part.getSize() != 0) {

                    File file = new File(PATH);
                    if (!file.exists() && !file.mkdirs())
                        throw new IllegalStateException("It's not possible to create a directory for user's uploaded file");

                    try ( InputStream inputStream = part.getInputStream();
                          FileOutputStream fos = new FileOutputStream(PATH + "/" + part.getSubmittedFileName()) )
                    {
                        int read = 0;
                        final byte[] bytes = new byte[1024];

                        while ((read = inputStream.read(bytes)) != -1) {
                            fos.write(bytes, 0, read);
                        }
                    }

                }
            }
            System.out.println("files match");

        } else {
            System.out.println("photo doesnt match regex or already exists");
        }

        resp.sendRedirect(req.getContextPath() + "/profile");
    }
}
