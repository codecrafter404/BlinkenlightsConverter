<?xml version="1.0" encoding="UTF-8"?>
<blm width="${width?c}" height="${height?c}" bits="${depth?c}" channels="1">
    <header>
        <title>Default Title</title>
        <description></description>
        <creator>BlinkenlightsConverter</creator>
        <author>Codecrafter_404</author>
        <email></email>
        <loop>no</loop>
        <duration>${duration?c}</duration>
    </header>

    <#list frames as frame>
    <frame duration="50">
        <#list frame.getRows() as row>
        <row>${row}</row>
        </#list>
    </frame>
    </#list>
</blm>