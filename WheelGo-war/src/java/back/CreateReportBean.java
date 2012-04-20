/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package back;

import ejb.CreateReportLocal;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author vlada
 */
@ManagedBean(name = "createReport")
@SessionScoped
public class CreateReportBean {

    @EJB
    private CreateReportLocal createReport;
    private UploadedFile file;
    private int state = CreateReportLocal.TYPE_UNSPEC;

    public String getName() {
        return createReport.getName();
    }

    public void setName(String name) {
        createReport.setName(name);
    }

    public void setDescription(String description) {
        createReport.setDescription(description);
    }

    public String getDescription() {
        return createReport.getDescription();
    }

    public Date getDate() {
        return createReport.getDate();
    }

    public void setDate(Date date) {
        createReport.setDate(date);
    }

    public void setLatitude(float latitude) {
        createReport.setLatitude(latitude);
    }
    
    public float getLatitude() {
        return createReport.getLatitude();
    }
    
    public void setLongitude(float longitude) {
        createReport.setLongitude(longitude);
    }
    
    public float getLongitude() {
        return createReport.getLongitude();
    }     
    
    public Date getExpiration() {
        if (state == CreateReportLocal.TYPE_PROBLEM_PRE || state == CreateReportLocal.TYPE_PROBLEM) {
            return createReport.getExpiration();
        } else {
            return new Date();
        }
    }

    public static byte[] InputStreamToByte(InputStream fis) throws FileNotFoundException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        try {
            for (int readNum; (readNum = fis.read(buf)) != -1;) {
                bos.write(buf, 0, readNum);

                System.out.println("read " + readNum + " bytes,");
            }
            byte[] bytes = bos.toByteArray();

            return bytes;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public void upload(FileUploadEvent event) {  
        file = event.getFile();
        FacesMessage msg;
        byte[] bytearray;
        try {
            bytearray = InputStreamToByte(file.getInputstream());
            /*
             * Konverze vstupu na obrazek:
             * 
             * BufferedImage srcImage = ImageIO.read(file.getInputstream());
            ByteArrayOutputStream os = new ByteArrayOutputStream();  
            ImageIO.write(srcImage, "jpg", os);
            bytearray = os.toByteArray();*/
            msg = new FacesMessage("Succesful", file.getFileName() + " is uploaded.");  
            FacesContext.getCurrentInstance().addMessage(null, msg);
            createReport.addPhoto(bytearray, null);
        } catch (Exception e) {
            e.printStackTrace();
            msg = new FacesMessage("Exception happen");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            this.state = CreateReportLocal.TYPE_UNSPEC;
        }
    } 
 
    public void setExpiration(Date date) {
        if (state == CreateReportLocal.TYPE_PROBLEM_PRE || state == CreateReportLocal.TYPE_PROBLEM) {
            createReport.setExpiration(date);
        }
    }

    public int getAccesibility() {
        if (state == CreateReportLocal.TYPE_PLACE_PRE || state == CreateReportLocal.TYPE_PLACE) {
            return createReport.getAccesibility();
        } else {
            return -1;
        }
    }

    public void setAccesibility(int accesibility) {
        if (state == CreateReportLocal.TYPE_PLACE_PRE || state == CreateReportLocal.TYPE_PLACE) {
            createReport.setAccesibility(accesibility);
        }
    }

    public void setState(String state) {
        if (state == null) {
            return;
        }

        if (state.equals("Problem")) {
            if (this.state == CreateReportLocal.TYPE_PROBLEM_PRE) {
                this.state = CreateReportLocal.TYPE_PROBLEM;
            } else if (this.state != CreateReportLocal.TYPE_PROBLEM) {
                this.state = CreateReportLocal.TYPE_PROBLEM_PRE;
            }
        } else if (state.equals("Tip")) {
            this.state = CreateReportLocal.TYPE_TIP;
        } else if (state.equals("Photo")) {
            this.state = CreateReportLocal.PHOTO;
        } else if (state.equals("Place")) {
            if (this.state == CreateReportLocal.TYPE_PLACE_PRE) {
                this.state = CreateReportLocal.TYPE_PLACE;
            } else if (this.state != CreateReportLocal.TYPE_PLACE) {
                this.state = CreateReportLocal.TYPE_PLACE_PRE;
            }
        }
    }

    public String getState() {
        switch (state) {
            case CreateReportLocal.TYPE_PROBLEM_PRE:
            case CreateReportLocal.TYPE_PROBLEM:
                return "Problem";

            case CreateReportLocal.TYPE_TIP:
                return "Tip";

            case CreateReportLocal.TYPE_PLACE_PRE:
            case CreateReportLocal.TYPE_PLACE:
                return "Place";
            case CreateReportLocal.PHOTO:
                return "Photo";
        }
        return "";
    }

    public String moveToNextStage() {
        switch (state) {
            case CreateReportLocal.TYPE_UNSPEC:
                break;
            case CreateReportLocal.TYPE_TIP:
                createReport.createTip();
                this.state = CreateReportLocal.PHOTO;
                break;
            case CreateReportLocal.TYPE_PROBLEM_PRE:
                createReport.preCreateProblem();
                this.state = CreateReportLocal.TYPE_PROBLEM;
                break;
            case CreateReportLocal.TYPE_PROBLEM:
                createReport.createProblem();
                this.state = CreateReportLocal.PHOTO;
                break;
            case CreateReportLocal.TYPE_PLACE_PRE:
                createReport.createPlacePre();
                this.state = CreateReportLocal.TYPE_PLACE;
                break;
            case CreateReportLocal.TYPE_PLACE:
                createReport.createPlace();
                this.state = CreateReportLocal.PHOTO;
                break;
            case CreateReportLocal.PHOTO:
                createReport.store();
                createReport.clear();
                this.state = CreateReportLocal.TYPE_UNSPEC; 
                return "index";
        }

        return "createReport";

    }

    public String cancelReport() {
        createReport.cancelReport();
        createReport.clear();
        this.state = CreateReportLocal.TYPE_UNSPEC; 
        return "index";
    }

    /**
     * Creates a new instance of CreateReportBean
     */
    public CreateReportBean() {
    }
}
