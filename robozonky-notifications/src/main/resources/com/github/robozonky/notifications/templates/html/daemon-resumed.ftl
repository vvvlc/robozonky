<p><@robozonky /> se zotavil po předchozí chybě a je znovu schopen fungovat.</p>

<dl>
    <dt>Robot byl pozastaven od:</dt>
    <dd>${data.since?datetime?iso_local_ms}</dd>

    <dt>Robot byl pozastaven do:</dt>
    <dd>${data.until?datetime?iso_local_ms}</dd>

    <dt>Celková doba pozastavení robota:</dt>
    <dd>${data.days} d., ${data.hours} hod., ${data.minutes} min. a ${data.seconds} s.</dd>
</dl>

