export default function genericDispatch(hasError, isLoading, success, method) {
    function specificDispatch(url, headers, body, extra) {
        return (dispatch) => {
            dispatch(isLoading(true))
            fetch(url, { headers, method, body, mode: 'cors'})
                .then((response) => {
                    console.log(response)
                    if (!response.ok) {
                        throw Error(response.statusText)
                    }
                    dispatch(isLoading(false))
                    return response
                })
                .then((response) => {
                    console.log(response)
                    return response.json()
                })
                .then((data) => {
                    console.log(data)
                    dispatch(success(data, extra))
                })
                .catch((error) => {
                    console.log(error)
                    dispatch(hasError(true))
                })
        }
    }
    return specificDispatch;
}