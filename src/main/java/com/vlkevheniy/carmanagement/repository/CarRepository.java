package com.vlkevheniy.carmanagement.repository;

import com.vlkevheniy.carmanagement.data.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {
}
