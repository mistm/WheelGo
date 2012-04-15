/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wrappers;

import dto.LogDTO;
import dto.PlaceDTO;
import dto.ProblemDTO;
import dto.TipDTO;
import dto.UserDTO;
import ejb.LogFacade;
import ejb.UserFacade;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author mist
 */
public class LogWrapper {
    private UserFacade userFacade = lookupUserFacadeLocal();

    private Integer idLog;

    public Integer getIdLog() {
        return idLog;
    }

    public void setIdLog(Integer idLog) {
        this.idLog = idLog;
    }

    private Integer idUser;
    private UserDTO user;
    
    private Integer idReport;
    private ProblemDTO problem;
    private PlaceDTO place;
    private TipDTO tip;
    
    private Date date;
    private Integer operation;
    
    public LogWrapper()
    {
        
    }
    
    public LogWrapper(LogDTO logDto)
    {
        idLog = logDto.getIdLog();
        date = logDto.getDate();
        operation = logDto.getOperation();
        idReport = logDto.getReport();
        idUser = logDto.getUser();
    }

    public UserDTO getUser()
    {
        if (idUser == null)
            return null;
        
        return (UserDTO)userFacade.find(idUser);
    }
    
    public void setUser(UserDTO user)
    {
        if (user == null)
        {
            this.user = null;
            this.idUser = null;
            return;
        }
        idUser = user.getIdUser();
        this.user = user;
    }
    
    public LogDTO getDto()
    {
        LogDTO ret = new LogDTO();
        ret.setIdLog(idLog);
        ret.setDate(date);
        if (user != null)
            ret.setUser(user);
        else
            ret.setUser(idUser);
        ret.setOperation(operation);
        return ret;
    }
    
    
    private UserFacade lookupUserFacadeLocal() {
        try {
            Context c = new InitialContext();
            return (UserFacade) c.lookup("java:global/WheelGo/WheelGo-ejb/UserFacade!ejb.Useracade");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

}
