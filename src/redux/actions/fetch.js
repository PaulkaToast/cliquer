export default function genericDispatch(hasError, isLoading, success, method) {
    function specificDispatch(url, headers, body, extra) {
        console.log(body)
        return (dispatch) => {
            dispatch(isLoading(true))
            fetch(url, { headers, method, body, mode: 'cors'})
                .then((response) => {
                    if (!response.ok) {
                        throw Error(response.statusText)
                    }
                    dispatch(isLoading(false))
                    return response
                })
                .then((response) => response.json())
                .then((data) => {
                    dispatch(success(data, extra))
                })
                .catch(() => dispatch(hasError(true)))
        }
    }
    return specificDispatch;
}