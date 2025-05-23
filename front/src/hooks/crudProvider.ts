import React from "react"
import { AxiosPromise, AxiosRequestConfig } from "axios";
import useAxios, { Options, RefetchOptions } from "axios-hooks"
import { BaseTree, Base } from "../models/Case";

export enum ItemMode {
    CREATE, EDIT, DELETE
}

function defaultFilterPredicate(current): (value, index: number, array) => unknown {
    return (x => x.id != current.id)
}

function defaultSearchPredicate(current): (value, index: number, array) => unknown {
    return (x => x.id === current.id)
}

function defaultConverter<T extends BaseTree & Base>(array: T[]) {
    const convertItem = (value: T): T => {
        return {
            ...value,
            label: value.name,
            parentId: value.parent ? value.parent.id : null
        }
    }

    
}

export function send(
    //filterPredicate: (value, index: number, array) => unknown = defaultFilterPredicate,
    fetch: (config?: AxiosRequestConfig, options?: RefetchOptions) => AxiosPromise<any>,
    requestConfig: AxiosRequestConfig): void {

    function transformBody(body) {
        if (typeof body == "number") {
            return body
        }
        return Object.fromEntries(Object.entries(body).map((value, key) => {
          if (value[1] instanceof Map) {
            let obj = Object.create(null);
            value[1].forEach((value, key) => obj[key] = value)
            value[1] = obj
          }
          return value;
      }))
    }
    if (requestConfig.data) {
        fetch({
            ...requestConfig,
            data: transformBody(requestConfig.data)
        });
    }
}

export interface AxiosProps {
    config: AxiosRequestConfig,
    options?: Options
}

export interface CurrentItemTools<T = any> {
    item: T | undefined,
    setItem: React.Dispatch<React.SetStateAction<T | undefined>>,
    setMode: React.Dispatch<React.SetStateAction<ItemMode | undefined>>,
    sender: () => void
}
export function useCurrentItem<T = any>({
        config, 
        options= {
            manual: true
        }
    }: AxiosProps
): CurrentItemTools  {
    const [item, setItem] = React.useState<T>();
    const [itemMode, setMode] = React.useState<ItemMode | undefined>();
    const [{ data }, refetch] = useAxios(
        config,
        options
      );

    const sender = React.useCallback(() => {
        switch(itemMode) {
            case ItemMode.CREATE: {
                config.method = "POST";
                break;
            }
            case ItemMode.EDIT: {
                config.method = "PUT";
                break;
            }
            case ItemMode.DELETE: {
                config.method = "DELETE";
                break;
            }
        }
        config.data = item;
        send(refetch, config);
    }, [item, itemMode]);

    return { item, setItem, setMode, sender }
}

export interface TableDataTool<T = any> {
    data: T,
    filterData: (current?: T, predicate?: (value: T, index: number, obj: T[]) => unknown) => void,
    updateData: (newData: T, predicate?: (value: T, index: number, obj: T[]) => unknown) => void
}

export function useTableData<T>({
            config, 
            options
        }: AxiosProps
    ): TableDataTool {
    const [{ data, loading }, refetch] = useAxios<T[]>(config, options);

    React.useEffect(() => {
        
    });
    
    function filterData(current?, predicate?: (value: T, index: number, obj: T[]) => unknown) {
        data && data.filter(
            predicate 
                ? predicate 
                : current 
                    ? defaultFilterPredicate(current) 
                    : () => true)
    }

    function updateData(
        newData: T, 
        predicate: (value: T, index: number, obj: T[]) => unknown =     defaultSearchPredicate) {
        if (data) {
            let replaceData = Array.from(data);
            let obj = replaceData.find(predicate);
            if (obj) {
                Object.assign(obj, newData);
            } else {
                replaceData.push(newData);
            }
          } 
    }

    return {data, filterData, updateData};
}