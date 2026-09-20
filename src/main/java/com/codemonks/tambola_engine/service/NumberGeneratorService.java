package com.codemonks.tambola_engine.service;

import java.util.Set;
public interface NumberGeneratorService {
    Integer generateNextNumber(Set<Integer> calledNumbers);
}