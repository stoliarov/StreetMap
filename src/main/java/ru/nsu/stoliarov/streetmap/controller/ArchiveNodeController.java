package ru.nsu.stoliarov.streetmap.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.stoliarov.streetmap.model.ICountHolder;
import ru.nsu.stoliarov.streetmap.service.NodeService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/archive")
public class ArchiveNodeController {
	
	private NodeService nodeService;
	
	@GetMapping
	@RequestMapping("/entries-count-by-user")
	public List<? extends ICountHolder> getEntriesCountByUser() {
		return nodeService.getEntriesCountByUser();
	}
	
	@GetMapping
	@RequestMapping("/entries-count-by-uid")
	public List<? extends ICountHolder> getEntriesCountByUid() {
		return nodeService.getEntriesCountByUid();
	}
}
