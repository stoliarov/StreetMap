package ru.nsu.stoliarov.streetmap.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.stoliarov.streetmap.model.ICountHolder;
import ru.nsu.stoliarov.streetmap.model.Node;
import ru.nsu.stoliarov.streetmap.service.NodeService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/db")
public class DbNodeController {
	
	private NodeService nodeService;
	
	// TODO: 15.03.20 работаем с нодами из базы
	
}
