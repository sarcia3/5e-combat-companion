package org.tcs.model.geometry;

public record RealPoint(double x, double y) {
  RealPoint add(RealPoint other) {
    return new RealPoint(this.x() + other.x(), this.y() + other.y());
  }

  RealPoint multiply(Double multiplier) {
    return new RealPoint(multiplier * this.x(), multiplier * this.y());
  }

  RealPoint divide(Double dividend) {
    return new RealPoint(this.x() / dividend, this.y() / dividend);
  }
}
