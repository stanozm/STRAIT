package fi.muni.cz.dataprovider.mapping;

import org.dozer.DozerBeanMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Radoslav Micko, 445611@muni.cz
 */
public class GitHubMapping implements BeanMapping {

    private final DozerBeanMapper dozerBeanMapper;
    
    /**
     * Initialize DozerBeanMapper with specified mapping file.
     * 
     * @param mappingFile name of mapping file.
     */
    public GitHubMapping(String mappingFile) {
        dozerBeanMapper = new DozerBeanMapper(Arrays.asList(mappingFile));
    }
    
    @Override
    public  <T> List<T> mapTo(Collection<?> objects, Class<T> mapToClass) {
        List<T> mappedCollection = new ArrayList<>();
        for (Object object : objects) {
            mappedCollection.add(dozerBeanMapper.map(object, mapToClass));
        }
        return mappedCollection;
    }
}