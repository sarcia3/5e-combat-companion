package org.tcs.model.geometry;

public record RealPoint(double x, double y) {
  public RealPoint add(RealPoint other) {
    return new RealPoint(this.x() + other.x(), this.y() + other.y());
  }

  public RealPoint multiply(double multiplier) {
    return new RealPoint(multiplier * this.x(), multiplier * this.y());
  }

  public RealPoint divide(double dividend) {
    return new RealPoint(this.x() / dividend, this.y() / dividend);
  }
}
