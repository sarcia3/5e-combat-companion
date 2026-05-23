package org.tcs.model.geometry;

public record RealPoint(double x, double y) {
  public RealPoint add(RealPoint other) {
    return new RealPoint(this.x() + other.x(), this.y() + other.y());
  }

  public RealPoint multiply(double multiplier) {
    return new RealPoint(multiplier * this.x(), multiplier * this.y());
  }

  public RealPoint divide(double divisor) {
    return new RealPoint(this.x() / divisor, this.y() / divisor);
  }

  public RealPoint floor() {
    return new RealPoint(Math.floor(x), Math.floor(y));
  }
}
