package org.tcs.model.geometry;

public record RealPoint(double x, double y) {
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof RealPoint(double x1, double y1))) return false;
    return (x1 == x) && (y1 == y);
  }

  @Override
  public int hashCode() {
    return (int) Math.floor(x * 1e6 + y);
  }
}
