package br.com.merkandi.rest;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


@Path("/b")
@WebListener
public class BService implements ServletContextListener
{
    private static long WAIT_DURATION_IN_MILLISECONDS = 0;
    private static int RETURN_HTTP_STATUS_CODE = 200;
    private static int ERROR_RATE = 0;

    private static Logger bServiceLogger = Logger.getLogger("BServiceLogger");
    private static FileHandler bServiceLoggerFilehandler;

    private static Long REQUEST_COUNTER = 0L;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getB()
    {
        ++REQUEST_COUNTER;

        try
        {
            Thread.sleep( WAIT_DURATION_IN_MILLISECONDS );
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        Random rn = new Random();
        if( ERROR_RATE > rn.nextInt(9) )
        {
            return Response.serverError().entity("Gone bad.").build();
        }

        return Response.status(RETURN_HTTP_STATUS_CODE).entity("B").build();
    }


    @POST
    @Path("/wait-duration")
    public Response postWaitDurationInMilliseconds(@FormParam("wait-duration") Long durationInMilliseconds)
    {
        if( durationInMilliseconds != null & durationInMilliseconds >= 0 )
        {
            WAIT_DURATION_IN_MILLISECONDS = durationInMilliseconds;
            bServiceLogger.info("wait duration updated:"+WAIT_DURATION_IN_MILLISECONDS + " ms");
        }
        return Response.noContent().build();
    }

    @POST
    @Path("/http-status-code")
    public Response postHttpStatusCode(@FormParam("http-status-code") Integer httpStatusCode)
    {
        if( httpStatusCode != null && httpStatusCode >= 0 )
        {
            RETURN_HTTP_STATUS_CODE = httpStatusCode;
            bServiceLogger.info("http status code updated:"+RETURN_HTTP_STATUS_CODE);
        }
        return Response.noContent().build();
    }

    @POST
    @Path("/error-rate")
    public Response postErrorRate(@FormParam("error-rate") Integer errorRate)
    {
        if( errorRate != null && errorRate >= 0
                && errorRate <= 100 )
        {
            ERROR_RATE = errorRate / 10;

            bServiceLogger.info("error rate updated:" + (ERROR_RATE * 10) + "%");
        }
        else
        {
            return Response.status(400).build();
        }
        return Response.noContent().build();
    }

    public static void init()
    {

    }

    @GET
    @Path("/count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRequestCount()
    {
        return Response.ok().entity(REQUEST_COUNTER).build();
    }

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        try
        {
            SimpleFormatter formatter = new SimpleFormatter();

            bServiceLoggerFilehandler = new FileHandler( sce.getServletContext().getInitParameter("B_LOG_PATH") );
            bServiceLoggerFilehandler.setFormatter(formatter);
            bServiceLogger.addHandler(bServiceLoggerFilehandler);

            bServiceLogger.info("\nBService running...");
        }
        catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}