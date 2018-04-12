package com.example.domain;

import java.time.LocalDate;
import lombok.Value;

@Value
public class HolidayRequest {
  private final String name;
  private final LocalDate startDate;
}
