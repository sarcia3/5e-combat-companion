package org.tcs.model.magic;

// todo implement in geometry
public sealed interface AreaOfEffect permits Sphere, Cube, Cylinder, Cone, Line
/*
not an enum because, sphere cube, cylinder etc. have defining dimensions like radius or length
*/
{}
