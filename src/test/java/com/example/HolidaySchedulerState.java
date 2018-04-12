package com.example;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.example.domain.HolidayScheduler;
import com.example.domain.HolidayScheduler.Response;
import com.example.domain.HolidayRequest;
import com.google.common.collect.Sets;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.Quoted;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HolidaySchedulerState extends Stage<HolidaySchedulerState> {

  private HolidayScheduler instance;
  private Response requestStatus;

  @BeforeScenario
  public void before() {
    instance = new HolidayScheduler(Sets.newHashSet(), Sets.newHashSet(), Sets.newHashSet(),
        Sets.newHashSet());
    log.info("before scenario HolidaySchedulerState");
  }

  public HolidaySchedulerState no_users_have_been_created() {
    Set<String> users = instance.listUsers();
    assertThat(users.size(), is(equalTo(0)));
    return self();
  }

  public HolidaySchedulerState user_$_requests_valid_holiday(@Quoted String userName) {
    LocalDate startDate = LocalDate.now().plus(1, ChronoUnit.WEEKS);
    requestStatus = instance.requestHoliday(new HolidayRequest(userName, startDate));
    return self();
  }

  public HolidaySchedulerState error_response_is_returned(@Quoted String message) {
    assertThat(requestStatus.message, is(equalTo(message)));
    return self();
  }

  public HolidaySchedulerState a_user_exist(@Quoted String userName) {
    instance.addUser(userName);
    return self();
  }

  public HolidaySchedulerState there_are_$_pending_requests_from_user_$(int count,
      @Quoted String userName) {
    Set<HolidayRequest> request = instance.listPendingRequests(userName);
    assertThat(count, is(equalTo(request.size())));
    return self();
  }

  public HolidaySchedulerState user_$_requests_holiday_in_the_past(String userName) {
    LocalDate startDate = LocalDate.now().minus(1, ChronoUnit.WEEKS);
    HolidayRequest request = new HolidayRequest(userName, startDate);
    requestStatus = instance.requestHoliday(request);
    return self();
  }

  public HolidaySchedulerState a_manager_exists(String managerName) {
    instance.addManager(managerName);
    return self();
  }

  public HolidaySchedulerState manager_$_accepts_request_from_$(String managerName, String userName) {
    Set<HolidayRequest> requests = instance.listPendingRequests(userName);
    assertThat(requests.size(), is(equalTo(1)));
    HolidayRequest request = requests.iterator().next();
    instance.acceptRequest(request);
    return self();
  }

  public HolidaySchedulerState there_are_$_accepted_requests_for_user_$(int count, String userName) {
    Set<HolidayRequest> acceptedRequests = instance.listAcceptedRequests(userName);
    assertThat(count, is(equalTo(acceptedRequests.size())));
    return self();
  }
}
