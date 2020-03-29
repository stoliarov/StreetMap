package ru.nsu.stoliarov.streetmap.service.retention;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nsu.stoliarov.streetmap.generated.Osm;
import ru.nsu.stoliarov.streetmap.model.Node;
import ru.nsu.stoliarov.streetmap.model.RetentionOptions;
import ru.nsu.stoliarov.streetmap.service.converter.NodeConverter;
import ru.nsu.stoliarov.streetmap.service.persistence.dao.NodeDao;
import ru.nsu.stoliarov.streetmap.service.persistence.dao.SaveMode;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;

@Slf4j
@Service
public class NodeRetentionStrategy implements IRetentionStrategy {

    private final Unmarshaller unmarshaller;

    private final NodeConverter nodeConverter;

    private final NodeDao nodeDao;

    public NodeRetentionStrategy(NodeConverter nodeConverter, NodeDao nodeDao) throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(Osm.Node.class);
        this.unmarshaller = jaxbContext.createUnmarshaller();
        this.nodeConverter = nodeConverter;
        this.nodeDao = nodeDao;
    }

    @Override
    public void saveStreetMapElement(XMLStreamReader reader, RetentionOptions retentionOptions) {

        try {
            JAXBElement<Osm.Node> osmNode = unmarshaller.unmarshal(reader, Osm.Node.class);

            Node node = nodeConverter.convert(osmNode.getValue());

            if (node == null) {
                log.warn("Failed to convert node: {}", osmNode);
                return;
            }

            if (retentionOptions == null) {
                retentionOptions = new RetentionOptions();
            }

            if (retentionOptions.getSaveMode() == null) {
                retentionOptions.setSaveMode(SaveMode.PREPARED_STATEMENT);
            }

            nodeDao.save(node, retentionOptions);

        } catch (JAXBException e) {
            log.warn("Failed to save street map element: {}", reader.getName());
            e.printStackTrace();
        }
    }

    @Override
    public void flush() {
        nodeDao.flush();
    }
}
