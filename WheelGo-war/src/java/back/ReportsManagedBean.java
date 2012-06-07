/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package back;

import dto.PhotoDTO;
import dto.ReportDTO;
import ejb.facades.interfaces.LogFacadeLocal;
import ejb.facades.interfaces.PhotoFacadeLocal;
import ejb.facades.interfaces.ReportFacadeLocal;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import wrappers.ReportWrapper;

/**
 *
 * @author mist
 */
@Named(value = "reportsManagedBean")
@SessionScoped
@ManagedBean(name = "reports")
public class ReportsManagedBean {

    float lowLatitude = 0;
    float upLatitude = 1;
    float lowLongitude = 0;
    float upLongitude = 1;
    @EJB
    private ReportFacadeLocal reportFacade;
    @EJB
    private PhotoFacadeLocal photoFacade;
    @EJB
    private LogFacadeLocal logFacade;

    /**
     * Creates a new instance of ReportsManagedBean
     */
    public ReportsManagedBean() {
    }

    public List<ReportWrapper> getReports() {
        List<ReportWrapper> list = new ArrayList<ReportWrapper>();
        List<ReportDTO> allReports = reportFacade.getAll();
        if (allReports != null) {
            for (ReportDTO r : allReports) {
                list.add(new ReportWrapper(r, photoFacade, logFacade));
                System.out.println("Reports.photos=" + r.getPhotosCollection());
            }
        }
        return list;
    }

    public List<ReportWrapper> getReportsArea() {
        List<ReportWrapper> list = new ArrayList<ReportWrapper>();
        List<ReportDTO> allReports = reportFacade.getArea(lowLatitude, upLatitude, lowLongitude, upLongitude, 100);
        if (allReports != null) {
            for (ReportDTO r : allReports) {
                list.add(new ReportWrapper(r, photoFacade, logFacade));
            }
        }
        return list;
    }

    public void removeReport(ReportWrapper report) {
        reportFacade.remove(report.getDto());
    }

    public float getLowLatitude() {
        return lowLatitude;
    }

    public void setLowLatitude(float lowLatitude) {
        this.lowLatitude = lowLatitude;
    }

    public float getLowLongitude() {
        return lowLongitude;
    }

    public void setLowLongitude(float lowLongitude) {
        this.lowLongitude = lowLongitude;
    }

    public float getUpLatitude() {
        return upLatitude;
    }

    public void setUpLatitude(float upLatitude) {
        this.upLatitude = upLatitude;
    }

    public float getUpLongitude() {
        return upLongitude;
    }

    public void setUpLongitude(float upLongitude) {
        this.upLongitude = upLongitude;
    }

    public String updateCoordinates() {
        return "reportAreaView";
    }

    /**
     * TODO: Ani jeden zpusob vykreslovani fotky nefunguje.
     */
    public StreamedContent getPhoto() throws IOException {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String photoIdStr = externalContext.getRequestParameterMap().get("photoId");
        
        Integer photoId = 1;
        try {
            photoId=Integer.parseInt(photoIdStr);
        } catch(Exception e) {
        }


        System.out.println("Drawing photo id = " + photoId);
        if (photoId == null) {
            return null;
        }
        PhotoDTO p = photoFacade.find(photoId);
        if (p != null) {
            byte[] photo = p.getImage();

            InputStream fis = new ByteArrayInputStream(photo);
            return new DefaultStreamedContent(fis, "image/png");
        } else {
            return null;
        }
    }
}
