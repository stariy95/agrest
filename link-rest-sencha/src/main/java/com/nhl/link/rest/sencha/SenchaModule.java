package com.nhl.link.rest.sencha;

import com.nhl.link.rest.runtime.encoder.IEncoderService;
import com.nhl.link.rest.runtime.protocol.IEntityUpdateParser;
import com.nhl.link.rest.runtime.processor.select.CreateResourceEntityStage;
import com.nhl.link.rest.runtime.processor.select.ParseRequestStage;
import com.nhl.link.rest.runtime.semantics.IRelationshipMapper;
import com.nhl.link.rest.sencha.parser.filter.ISenchaFilterParser;
import com.nhl.link.rest.sencha.parser.filter.SenchaFilterParser;
import org.apache.cayenne.di.Binder;
import org.apache.cayenne.di.Module;

/**
 * @since 2.10
 */
public class SenchaModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(ParseRequestStage.class).to(SenchaParseRequestStage.class);
        binder.bind(CreateResourceEntityStage.class).to(SenchaConstructResourceEntityStage.class);
        binder.bind(IEncoderService.class).to(SenchaEncoderService.class);
        binder.bind(IRelationshipMapper.class).to(SenchaRelationshipMapper.class);
        binder.bind(ISenchaFilterConstructor.class).to(SenchaFilterConstructor.class);
        binder.bind(ISenchaFilterParser.class).to(SenchaFilterParser.class);
        binder.bind(IEntityUpdateParser.class).to(SenchaUpdateParser.class);
    }
}