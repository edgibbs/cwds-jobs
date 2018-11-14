package gov.ca.cwds.jobs.cap.users.service;

import com.google.inject.Inject;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserAndOperation;
import gov.ca.cwds.idm.dto.UsersPage;
import gov.ca.cwds.jobs.cap.users.inject.PerryApiPassword;
import gov.ca.cwds.jobs.cap.users.inject.PerryApiUrl;
import gov.ca.cwds.jobs.cap.users.inject.PerryApiUser;
import gov.ca.cwds.jobs.cap.users.service.exception.IdmServiceException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdmServiceImpl implements IdmService {

  private static final String PAGINATION_TOKEN = "paginationToken";
  private static final String DATETIME_PARAM = "date";
  private static final String DATETIME_FORMAT_PATTERN = "yyyy-MM-dd-HH.mm.ss.SSS";

  private static final Logger LOGGER = LoggerFactory.getLogger(IdmServiceImpl.class);
  private static final int BATCH_SIZE = 7;


  @Inject
  private Client client;

  @Inject
  @PerryApiUrl
  private String apiURL;

  @Inject
  @PerryApiUser
  private String perryApiUser;

  @Inject
  @PerryApiPassword
  private String perryApiPassword;

  private String basicAuthHeader;

  @Inject
  public void init() {
    String authString = perryApiUser + ":" + perryApiPassword;
    byte[] authEncBytes = Base64.encodeBase64(authString.getBytes(StandardCharsets.UTF_8));
    String authStringEnc = new String(authEncBytes, StandardCharsets.UTF_8);
    basicAuthHeader = "Basic " + authStringEnc;
  }

  @Override
  public UsersPage getUserPage(String paginationToken) {
    return client.target(apiURL + "/users")
        .queryParam(PAGINATION_TOKEN, paginationToken)
        .request(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, basicAuthHeader)
        .get(UsersPage.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<User> getUsersByRacfIds(List<String> racfIds) {
    List<List<String>> batches = ListUtils.partition(racfIds, BATCH_SIZE);
    LOGGER.info("Start working on {} batches", batches.size());

    List<User> users = new ArrayList<>();

    int i = 0;
    for (List<String> batch : batches) {
      LOGGER.info("APP IS PROCESSING THE BATCH #{}", i++);
      users.addAll(processRequest(batch));
      try {
        LOGGER.info("SLEEP FOR 700");
        Thread.sleep(700);
      } catch (InterruptedException e) {
        LOGGER.warn("THREAD ERROR!!!");
        Thread.currentThread().interrupt();
      }
    }

    return  users;
    }

  private List<User> processRequest(List<String> racfIds) {
    Response response = client
        .target(apiURL + "/users/search")
        .request(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, basicAuthHeader)
        .post(Entity.entity(racfIds, MediaType.APPLICATION_JSON));

    if (response.getStatus() != Status.OK.getStatusCode()) {
      LOGGER.warn("IDM search by RACFIDs responded with status {}", response.getStatus());
      throw new IdmServiceException();
    }

    return response.readEntity(new GenericType<List<User>>() {
    });
  }

  @Override
  public List<UserAndOperation> getCapChanges(LocalDateTime savePointTime) {

    String dateTime = savePointTime.format(DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN));

    Response response = client
        .target(apiURL + "/users/failed-operations")
        .queryParam(DATETIME_PARAM, dateTime)
        .request(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, basicAuthHeader)
        .get();

    if (response.getStatus() != Status.OK.getStatusCode()) {
      LOGGER.warn("IDM getCapChanges responded with status {}", response.getStatus());
      throw new IdmServiceException();
    }

    return response.readEntity(new GenericType<List<UserAndOperation>>() {
    });
  }
}
