import React, { Component } from 'react'
import { Button } from 'reactstrap'
import { connect } from 'react-redux'

import '../css/SearchResults.css'

class SearchResults extends Component {
  componentWillReceiveProps = (nextProps) => {
    const { query, category } = nextProps.match.params
    //API call for searching goes here, redux
  }

  renderGroupPreview = (group, i) => {
    //Sprint 2 or 3
  }

  renderUserPreview = (user, i) => {
    //may need to change button function to be a prop later (to allow reusing for group member searching)
    return (
      <div className="user-result" key={i}>
        <div className="user-icon">ICON</div>
        <div className="user-info">
            <div className="user-name">{user.name}</div>
            <div className="user-reputation">{user.reputation}</div>
            <Button color="success" onClick={() => this.props.sendFriendRequest(user)}>Send Friend Request</Button>
        </div>
      </div>
    )
  }

  renderResults = (renderResult) => {
    const { results } = this.props
    if(results) {
      return (
        <ul className="results-list">
          {results.map((result, i) => renderResult(result, i))}
        </ul>
      )
    }
  }

  render() {
    const { category } = this.props.match.params
    return (
      <div className="SearchResults">
        { category === 'group'
          ? this.renderResults(this.renderGroupPreview) 
          : this.renderResults(this.renderUserPreview)
        }
      </div>
    )
  }
}

const mapStateToProps = (state) => {
	return {
    /*results*/
	}
}

export default connect(mapStateToProps)(SearchResults)