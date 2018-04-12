package com.example.domain;

import static lombok.Lombok.checkNotNull;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.util.Strings;

public class HolidayScheduler {

  private final Set<String> users;
  private final Set<String> managers;
  private final Set<HolidayRequest> pendingRequests;
  private final Set<HolidayRequest> approvedRequests;

  public HolidayScheduler(Set<String> users, Set<String> manager, Set<HolidayRequest> holidays,
      Set<HolidayRequest> approvedRequests) {
    this.users = users;
    this.managers = manager;
    this.pendingRequests = holidays;
    this.approvedRequests = approvedRequests;
  }

  public void addUser(String name) {
    checkNotNull(name, "Name should not be null");
    this.users.add(name);
  }

  public Set<String> listUsers() {
    return Collections.unmodifiableSet(users);
  }

  public Response requestHoliday(HolidayRequest holidayRequest) {
    String name = holidayRequest.getName();
    if (Strings.isBlank(name)) {
      return Response.fail("Name can not be empty");
    }
    if (!userIsPresent(name)) {
      return Response.fail(String.format("User '%s' not found", name));
    }
    if (!startDateIsValid(holidayRequest.getStartDate())) {
      return Response.fail("The dates can not be in the past");
    }
    pendingRequests.add(holidayRequest);
    return Response.success("Requests saved");
  }

  private boolean startDateIsValid(LocalDate startDate) {
    return LocalDate.now().plusDays(1).isBefore(startDate);
  }

  private boolean userIsPresent(String name) {
    return users.stream().filter(name::equals).count() > 0;
  }

  public Set<HolidayRequest> listPendingRequests(String userName) {
    return pendingRequests.stream().filter(req -> req.getName().equals(userName))
        .collect(Collectors.toSet());
  }

  public void addManager(String managerName) {
    managers.add(managerName);
  }

  public Response acceptRequest(HolidayRequest request) {
    if (pendingRequests.remove(request)) {
      approvedRequests.add(request);
    }
    return Response.success("Request accepted");
  }

  public Set<HolidayRequest> listAcceptedRequests(String userName) {
    return approvedRequests.stream().filter(req -> req.getName().equals(userName))
        .collect(Collectors.toSet());
  }

  public static class Response {

    public final boolean status;
    public final String message;

    private Response(boolean status, String message) {
      this.status = status;
      this.message = message;
    }

    public static Response fail(String message) {
      return new Response(false, message);
    }

    public static Response success(String message) {
      return new Response(true, message);
    }
  }
}
