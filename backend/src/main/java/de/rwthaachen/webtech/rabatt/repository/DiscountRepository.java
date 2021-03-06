package de.rwthaachen.webtech.rabatt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.rwthaachen.webtech.rabatt.model.Discount;
import de.rwthaachen.webtech.rabatt.model.User;

public interface DiscountRepository extends JpaRepository<Discount, Integer> {
  List<Discount> findByCreator(User user);

  List<Discount> findByTitle(String title);
}
