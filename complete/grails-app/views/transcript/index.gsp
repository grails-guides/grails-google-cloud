<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'transcript.label', default: 'Transcript')}" />
    <title><g:message code="default.list.label" args="[entityName]" /></title>
</head>
<body>
<a href="#list-transcript" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
    </ul>
</div>

<g:uploadForm name="updloadAudio" action="updloadAudio">
    <input type="file" name="audioFile" />
    <fieldset class="buttons">
        <input class="save" type="submit" value="${message(code: 'transcript.audioFile.upload.label', default: 'Upload & Transcript Audio File')}" />
    </fieldset>
</g:uploadForm>

</body>
</html>