import React from "react"

export default function useActiveTab() {
    const [activeTab, setActiveTab] = React.useState<number>();

    function toggle(tabId: number) {
        if (activeTab !== tabId) 
            setActiveTab(tabId);
    }

  return [{ activeTab, toggle }]
}